<template>
  <div class="page">
    <el-card>
      <h2>{{ isEdit ? '编辑物品' : '发布物品' }}</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price"><el-input-number v-model="form.price" :min="0.01" :precision="2" /></el-form-item>
        <el-form-item label="成色" prop="conditionLevel">
          <el-select v-model="form.conditionLevel">
            <el-option label="全新" value="全新" />
            <el-option label="九成新" value="九成新" />
            <el-option label="八成新" value="八成新" />
            <el-option label="正常使用" value="正常使用" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description"><el-input v-model="form.description" type="textarea" :rows="5" /></el-form-item>
        <el-form-item label="图片">
          <el-upload list-type="picture-card" :http-request="upload" :file-list="fileList" :on-remove="removeImage">
            <span>上传</span>
          </el-upload>
        </el-form-item>
        <el-button type="primary" @click="submit">保存</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createItem, getCategories, getItemDetail, updateItem, uploadFile } from '../../api/item'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref()
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
  if (isEdit.value) await updateItem(route.params.id, form)
  else await createItem(form)
  router.push('/mine/items')
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
