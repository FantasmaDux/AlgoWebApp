import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth'
import './Login.css'

function Login() {
  const navigate = useNavigate()
  const [isLogin, setIsLogin] = useState(true)
  const [email, setEmail] = useState('')
  const [code, setCode] = useState('')
  const [step, setStep] = useState('form')
  const [message, setMessage] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSendCode = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    try {
      await authApi.sendCode(email, isLogin)
      setStep('code')
      setMessage(`Код отправлен на ${email}`)
    } catch (error) {
      setMessage(error.response?.data?.error || 'Ошибка отправки')
    } finally {
      setLoading(false)
    }
  }

  const handleConfirm = async (e) => {
    e.preventDefault()
    setLoading(true)
    setMessage('')
    try {
      const response = await authApi.confirm(email, code, isLogin)
      if (isLogin) {
        localStorage.setItem('accessToken', response.data.accessToken)
        localStorage.setItem('userId', response.data.accountId);
        localStorage.setItem('refreshToken', response.data.refreshToken)
        navigate('/')
      } else {
        setMessage('Регистрация успешна! Теперь войдите.')
        setIsLogin(true)
        setStep('form')
        setCode('')
      }
    } catch (error) {
      setMessage(error.response?.data?.error || 'Ошибка подтверждения')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <h1 className="title">{isLogin ? 'Вход в аккаунт' : 'Регистрация'}</h1>

        {step === 'form' ? (
          <form onSubmit={handleSendCode}>
            <div className="input-group">
              <label>email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="email@example.com"
                required
              />
            </div>

            {message && <div className="message error">{message}</div>}

            <div className="button-group">
              <button type="submit" className="btn-primary" disabled={loading}>
                {loading ? '...' : (isLogin ? 'войти' : 'зарегистрироваться')}
              </button>
              <button
                type="button"
                className="btn-secondary"
                onClick={() => {
                  setIsLogin(!isLogin)
                  setMessage('')
                }}
              >
                {isLogin ? 'регистрация' : 'вход'}
              </button>
            </div>
          </form>
        ) : (
          <form onSubmit={handleConfirm}>
            <div className="input-group">
              <label>email</label>
              <input type="email" value={email} disabled />
            </div>

            <div className="input-group">
              <label>код подтверждения</label>
              <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="000000"
                required
                autoFocus
              />
            </div>

            {message && <div className="message success">{message}</div>}

            <div className="button-group">
              <button type="button" className="btn-secondary" onClick={() => setStep('form')}>
                назад
              </button>
              <button type="submit" className="btn-primary" disabled={loading}>
                {loading ? '...' : 'подтвердить'}
              </button>
            </div>
          </form>
        )}
      </div>

      <div className="footer">
        MVP patterns | мы вам перезвоним
      </div>
    </div>
  )
}

export default Login