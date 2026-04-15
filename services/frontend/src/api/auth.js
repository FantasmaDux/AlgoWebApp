import axios from 'axios'

const API_GATEWAY = 'http://localhost:8080'

const api = axios.create({
  baseURL: '',  // пустая строка — запросы на тот же хост
})

// Добавляем токен к каждому запросу
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true
      const refreshToken = localStorage.getItem('refreshToken')
      
      if (refreshToken) {
        try {
          const response = await axios.post(`${API_GATEWAY}/auth/v1/refresh`, { refreshToken })
          localStorage.setItem('accessToken', response.data.accessToken)
          originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`
          return api(originalRequest)
        } catch (e) {
          localStorage.clear()
          window.location.href = '/login'
        }
      }
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  // Регистрация - отправка кода
  sendCode: (email, isLogin) => 
    api.post(`/auth/v1/${isLogin ? 'login/sendCodeEmail' : 'registration'}`, { email }),
  
  // Подтверждение - проверка кода
  confirm: (email, code, isLogin) => 
    api.post(`/auth/v1/${isLogin ? 'login/confirmEmail' : 'registration/confirmEmail'}`, { email, code }),
}

export default api