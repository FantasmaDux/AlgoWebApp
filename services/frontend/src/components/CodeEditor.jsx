import React, { useState } from 'react';

function CodeEditor({ onSubmit, isSubmitting }) {
  const [code, setCode] = useState(`// Пример решения на Java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
`);
  const [language, setLanguage] = useState('java');
  const [output, setOutput] = useState('');

  const handleSubmit = async () => {
    setOutput('🔄 Компиляция и проверка...');
    try {
      const result = await onSubmit(code, language);
      if (result.data.success) {
        setOutput(`✅ ${result.data.message}\n\nВремя выполнения: ${result.data.executionTimeMs}ms\nПройдено тестов: ${result.data.passedTests}/${result.data.totalTests}`);
      } else {
        setOutput(`❌ ${result.data.message}\n\nПройдено тестов: ${result.data.passedTests}/${result.data.totalTests}`);
      }
    } catch (error) {
      setOutput(`❌ Ошибка: ${error.response?.data?.message || error.message}`);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ marginBottom: '12px', display: 'flex', gap: '12px', alignItems: 'center' }}>
        <label>
          Язык:
          <select value={language} onChange={(e) => setLanguage(e.target.value)} style={{ marginLeft: '8px', padding: '4px' }}>
            <option value="java">Java</option>
          </select>
        </label>
        <button onClick={handleSubmit} disabled={isSubmitting} style={{
          padding: '8px 16px',
          background: 'var(--accent)',
          color: 'white',
          border: 'none',
          borderRadius: '6px',
          cursor: 'pointer'
        }}>
          {isSubmitting ? 'Проверка...' : '🚀 Проверить решение'}
        </button>
      </div>
      
      <textarea
        value={code}
        onChange={(e) => setCode(e.target.value)}
        style={{
          width: '100%',
          height: '300px',
          fontFamily: 'var(--mono)',
          fontSize: '14px',
          padding: '12px',
          background: 'var(--code-bg)',
          border: '1px solid var(--border)',
          borderRadius: '8px',
          color: 'var(--text-h)',
          resize: 'vertical'
        }}
      />
      
      {output && (
        <pre style={{
          marginTop: '12px',
          padding: '12px',
          background: 'var(--code-bg)',
          border: '1px solid var(--border)',
          borderRadius: '8px',
          overflow: 'auto',
          fontFamily: 'var(--mono)',
          fontSize: '13px'
        }}>
          {output}
        </pre>
      )}
    </div>
  );
}

export default CodeEditor;