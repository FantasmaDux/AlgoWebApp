import React from 'react';

function TaskDescription({ task, isSolved }) {
  if (!task) return <div>Загрузка...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
        <h2 style={{ margin: 0 }}>{task.name}</h2>
        {isSolved && (
          <span style={{
            background: '#22c55e',
            color: 'white',
            padding: '4px 12px',
            borderRadius: '20px',
            fontSize: '14px'
          }}>
            ✅ Решена
          </span>
        )}
      </div>
      <div style={{
        background: 'var(--code-bg)',
        padding: '16px',
        borderRadius: '8px',
        marginBottom: '16px'
      }}>
        <h3>Описание:</h3>
        <p>{task.description}</p>
      </div>
      <div style={{
        background: 'var(--accent-bg)',
        padding: '16px',
        borderRadius: '8px',
        borderLeft: `4px solid var(--accent)`
      }}>
        <h3>💡 Пример паттерна:</h3>
        <pre style={{ margin: 0, fontFamily: 'var(--mono)', fontSize: '13px' }}>
          {task.patternName || `Связан с паттерном #${task.patternId}`}
        </pre>
      </div>
    </div>
  );
}

export default TaskDescription;