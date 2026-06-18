<template>
  <div class="page">
    <el-card class="form-card">
      <div class="page-header">
        <h2>{{ isEdit ? '编辑物品' : '发布物品' }}</h2>
        <p class="page-subtitle">{{ isEdit ? '更新您的闲置物品信息' : '分享您的闲置，让它找到新主人' }}</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" label-position="top">
        <el-row :gutter="24">
          <el-col :span="16">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="给物品起一个吸引人的标题" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="5" placeholder="详细描述物品的状态、使用情况等" />
            </el-form-item>
            <el-form-item label="图片">
              <el-upload list-type="picture-card" :http-request="upload" :file-list="fileList" :on-remove="removeImage">
                <div class="upload-trigger">
                  <span class="upload-icon">+</span>
                  <span>上传图片</span>
                </div>
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <div class="side-panel">
              <el-form-item label="分类" prop="categoryId">
                <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
                  <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="价格" prop="price">
                <el-input-number v-model="form.price" :min="0.01" :precision="2" style="width: 100%" />
              </el-form-item>
              <el-form-item label="成色" prop="conditionLevel">
                <el-select v-model="form.conditionLevel" style="width: 100%">
                  <el-option label="全新" value="全新" />
                  <el-option label="九成新" value="九成新" />
                  <el-option label="八成新" value="八成新" />
                  <el-option label="正常使用" value="正常使用" />
                </el-select>
              </el-form-item>
              <el-button type="primary" size="large" style="width: 100%; margin-top: 12px" :loading="submitting" @click="submit">
                {{ submitting ? 'AI 审核中…' : (isEdit ? '保存修改' : '发布物品') }}
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createItem, getCategories, getItemDetail, updateItem, uploadFile } from '../../api/item'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref()
const submitting = ref(false)
const categories = ref([])
const fileList = ref([])
const form = reactive({ title: '', categoryId: null, description: '', price: 1, conditionLevel: '九成新', imageUrls: [] })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  conditionLevel: [{ required: true, message: '请选择成色', trigger: 'change' }]
}

async function upload(option) {
  const data = await uploadFile(option.file)
  form.imageUrls.push(data.url)
  fileList.value.push({ name: option.file.name, url: data.url })
  option.onSuccess(data)
}

function removeImage(file) {
  form.imageUrls = form.imageUrls.filter((url) => url !== file.url)
  fileList.value = fileList.value.filter((f) => f.url !== file.url)
}

async function submit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) await updateItem(route.params.id, form, { timeout: 30000 })
    else await createItem(form, { timeout: 30000 })
    ElMessage.success(isEdit.value ? '修改成功，商品已上架' : '发布成功，商品已通过 AI 审核并上架')
    router.push('/mine/items')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  categories.value = await getCategories()
  if (isEdit.value) {
    const detail = await getItemDetail(route.params.id)
    Object.assign(form, detail.item)
    form.imageUrls = detail.images.map((i) => i.imageUrl)
    fileList.value = detail.images.map((i) => ({ name: i.imageUrl, url: i.imageUrl }))
  }
})
</script>

<style scoped>
.side-panel {
  padding: 24px;
  background: rgba(30, 82, 64, 0.04);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-gold);
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: var(--color-muted);
  font-size: 0.82rem;
}

.upload-icon {
  font-size: 1.5rem;
  color: var(--color-gold);
}

:deep(.el-upload--picture-card) {
  border: 1px dashed var(--border-gold-strong) !important;
  border-radius: var(--radius-md) !important;
  background: rgba(255, 252, 247, 0.5) !important;
}

:deep(.el-upload--picture-card:hover) {
  border-color: var(--color-emerald) !important;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--color-text-soft);
}
</style>
