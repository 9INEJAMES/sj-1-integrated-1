import { defineStore, acceptHMRUpdate } from 'pinia'
import { ref } from 'vue'

export const useToast = defineStore('toast', () => {
  const currToast = ref({ style: '', msg: '' })
  const changeToast = (isSuccess, msg) => {
    isSuccess
      ? (currToast.value.style = 'alert-success')
      : (currToast.value.style = 'alert-error')

    currToast.value.msg = msg
    setTimeout(() => {
      resetToast()
    }, 2000)
  }
  const resetToast = () => {
    currToast.value = { style: '', msg: '' }
  }
  return { currToast, changeToast, resetToast }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useToast, import.meta.hot))
}
