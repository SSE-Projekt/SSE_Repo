
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

// Token aus localStorage holen 
function getToken() {
  const token = localStorage.getItem('token')
  if (!token) {
    throw new Error('Nicht eingeloggt')
  }
  return token
}

// ============ NOTES CRUD ============

/**
 * Alle meine Notizen abrufen
 * GET /api/notes/my
 */
export async function getMyNotes() {
  const token = getToken()
  
  const response = await axios.get(`${API_BASE_URL}/notes/my`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data  // Liste von Notizen
}

/**
 * Alle öffentlichen Notizen
 * GET /api/notes/public
 */
export async function getPublicNotes() {
  // Kein Token nötig für öffentliche Notizen
  const response = await axios.get(`${API_BASE_URL}/notes/public`)
  return response.data
}

/**
 * Eine spezifische Notiz
 * GET /api/notes/{id}
 */
export async function getNote(noteId) {
  const token = getToken()
  
  const response = await axios.get(`${API_BASE_URL}/notes/${noteId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}

/**
 * Neue Notiz erstellen
 * POST /api/notes
 */
export async function createNote(noteData) {
  const token = getToken()
  
  const response = await axios.post(`${API_BASE_URL}/notes`, noteData, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data  // Die erstellte Notiz
}

/**
 * Notiz aktualisieren
 * PUT /api/notes/{id}
 */
export async function updateNote(noteId, noteData) {
  const token = getToken()
  
  const response = await axios.put(`${API_BASE_URL}/notes/${noteId}`, noteData, {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}

/**
 * Notiz löschen
 * DELETE /api/notes/{id}
 */
export async function deleteNote(noteId) {
  const token = getToken()
  
  const response = await axios.delete(`${API_BASE_URL}/notes/${noteId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}

/**
 * Notizen suchen
 * GET /api/notes/search?q=query
 */
export async function searchNotes(query) {
  const token = getToken()
  
  const response = await axios.get(`${API_BASE_URL}/notes/search`, {
    params: { q: query },  // wird zu ?q=query
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}

/**
 * Current User Info
 * GET /api/me
 */
export async function getCurrentUser() {
  const token = getToken()
  
  const response = await axios.get(`${API_BASE_URL}/me`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  
  return response.data
}

/**
 * Health Check (kein Token nötig)
 * GET /api/health
 */
export async function checkHealth() {
  const response = await axios.get(`${API_BASE_URL}/health`)
  return response.data
}