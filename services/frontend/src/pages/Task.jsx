import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import CodeEditor from '../components/CodeEditor';
import TaskDescription from '../components/TaskDescription';
import { getTask, submitSolution, isTaskSolved } from '../api/solver';

function Task() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [task, setTask] = useState(null);
  const [isSolved, setIsSolved] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    loadTask();
  }, [id]);

  const loadTask = async () => {
    try {
      const userId = localStorage.getItem('userId');
      const [taskRes, solvedRes] = await Promise.all([
        getTask(id, userId),
        isTaskSolved(id)
      ]);
      setTask(taskRes.data);
      setIsSolved(solvedRes.data);
    } catch (error) {
      console.error('Error loading task:', error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (code, language) => {
    setSubmitting(true);
    try {
      const result = await submitSolution(parseInt(id), code, language);
      if (result.data.success) {
        setIsSolved(true);
      }
      return result;
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return (
    <>
      <Navbar />
      <div style={{ padding: '24px', textAlign: 'center' }}>Загрузка задачи...</div>
    </>
  );

  if (!task) return (
    <>
      <Navbar />
      <div style={{ padding: '24px', textAlign: 'center' }}>Задача не найдена</div>
    </>
  );

  return (
    <>
      <Navbar />
      <div style={{ display: 'flex', height: 'calc(100vh - 60px)' }}>
        {/* Левая панель — описание задачи */}
        <div style={{
          width: '40%',
          overflow: 'auto',
          padding: '20px',
          borderRight: '1px solid var(--border)'
        }}>
          <TaskDescription task={task} isSolved={isSolved} />
        </div>
        
        {/* Правая панель — компилятор */}
        <div style={{
          width: '60%',
          overflow: 'auto',
          padding: '20px'
        }}>
          <CodeEditor onSubmit={handleSubmit} isSubmitting={submitting} />
        </div>
      </div>
    </>
  );
}

export default Task;