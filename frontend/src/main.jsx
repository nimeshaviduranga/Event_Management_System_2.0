import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.jsx'
import './assets/styles/global.css'

/**
 * Main entry point for the application
 * We wrap the App component with:
 * - BrowserRouter: Enables routing functionality
 * - StrictMode: Helps catch potential issues during development
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
)