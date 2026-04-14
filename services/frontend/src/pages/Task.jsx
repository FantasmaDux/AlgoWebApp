import React from 'react'
import { useParams } from 'react-router-dom'

function Task() {
  const { id } = useParams()
  
  return (
    <div>
      <h1>Task Page</h1>
      <p>ID задачи: {id}</p>
      <p>Здесь будет компилятор и описание задачи</p>
    </div>
  )
}

export default Task