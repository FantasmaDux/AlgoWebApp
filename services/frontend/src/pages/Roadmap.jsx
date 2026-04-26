import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getPatterns, getSolvedTasks } from '../api/solver';

function Roadmap() {
  const navigate = useNavigate();
  const [patterns, setPatterns] = useState([]);  // ← изначально пустой массив
  const [solvedTaskIds, setSolvedTaskIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [patternsRes, solvedRes] = await Promise.all([
        getPatterns(),
        getSolvedTasks()
      ]);
      
      // Проверяем, что patternsRes.data — это массив
      const patternsData = Array.isArray(patternsRes.data) ? patternsRes.data : [];
      const solvedData = solvedRes.data?.solvedTaskIds || [];
      
      setPatterns(patternsData);
      setSolvedTaskIds(solvedData);
    } catch (error) {
      console.error('Error loading data:', error);
      setError(error.response?.data?.message || 'Ошибка загрузки данных');
      if (error.response?.status === 401) {
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <>
        <Navbar />
        <div style={{ padding: '24px', textAlign: 'center' }}>Загрузка...</div>
      </>
    );
  }

  if (error) {
    return (
      <>
        <Navbar />
        <div style={{ padding: '24px', textAlign: 'center', color: 'red' }}>Ошибка: {error}</div>
      </>
    );
  }

  if (!patterns.length) {
    return (
      <>
        <Navbar />
        <div style={{ padding: '24px', textAlign: 'center' }}>
          Нет доступных паттернов. Проверьте подключение к серверу.
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div style={{ padding: '24px' }}>
        <h1>📚 Roadmap</h1>
        
        {patterns.map(pattern => (
          <div key={pattern.id} style={{
            marginBottom: '32px',
            border: '1px solid var(--border)',
            borderRadius: '12px',
            overflow: 'hidden'
          }}>
            <div style={{
              padding: '16px 20px',
              background: 'var(--code-bg)',
              borderBottom: '1px solid var(--border)'
            }}>
              <h2 style={{ margin: 0 }}>{pattern.name}</h2>
              <p style={{ marginTop: '8px', fontSize: '14px' }}>{pattern.description}</p>
              <pre style={{
                marginTop: '12px',
                padding: '8px',
                background: 'var(--bg)',
                borderRadius: '6px',
                fontSize: '12px',
                overflow: 'auto'
              }}>
                {pattern.example}
              </pre>
            </div>
            
            <div>
              {Array.isArray(pattern.tasks) && pattern.tasks.map(task => (
                <div
                  key={task.id}
                  onClick={() => navigate(`/task/${task.id}`)}
                  style={{
                    padding: '12px 20px',
                    borderBottom: '1px solid var(--border)',
                    cursor: 'pointer',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}
                >
                  <span>
                    <span style={{ fontWeight: 500 }}>{task.number}. {task.name}</span>
                  </span>
                  {solvedTaskIds.includes(task.id) && (
                    <span style={{
                      background: '#22c55e',
                      color: 'white',
                      padding: '2px 8px',
                      borderRadius: '20px',
                      fontSize: '12px'
                    }}>✅ Решена</span>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </>
  );
}

export default Roadmap;