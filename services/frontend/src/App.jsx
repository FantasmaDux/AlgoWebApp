import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Roadmap from './pages/Roadmap'
import Task from './pages/Task'
import ProtectedRoute from './components/ProtectedRoute.jsx'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={
        <ProtectedRoute>
          <Roadmap />
        </ProtectedRoute>
      } />
      <Route path="/task/:id" element={
        <ProtectedRoute>
          <Task />
        </ProtectedRoute>
      } />
    </Routes>
  )
}

export default App