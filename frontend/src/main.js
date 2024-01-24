import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faMagnifyingGlass,
  faCircleChevronLeft,
  faCircleChevronRight,
  faPaperPlane,
  fas
} from '@fortawesome/free-solid-svg-icons'
import { faCalendarCheck, faUser, far } from '@fortawesome/free-regular-svg-icons'
import { quillEditor } from 'vue3-quill'

import App from './App.vue'
import router from './router'

library.add(faMagnifyingGlass, faCircleChevronLeft, faCircleChevronRight, faPaperPlane)
library.add(faCalendarCheck, faUser, fas, far)

const app = createApp(App)
axios.defaults.baseURL = 'http://localhost:8080'
app.config.globalProperties.$axios = axios

app.component('font-awesome-icon', FontAwesomeIcon)

app.use(quillEditor)
app.use(createPinia())
app.use(router)

app.mount('#app')
