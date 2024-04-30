const url = import.meta.env.VITE_BASE_URL

async function getAllTasks() {
  try {
    const data = await fetch(`${url}`)
    const result = await data.json()
    return result
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function getTaskById(id) {
  try {
    const data = await fetch(`${url}/${id}`)
    const result = await data.json()
    return result
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function addTask(obj) {
  try {
    const response = await fetch(`${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ ...obj }),
    })
    const result = await response.json()
    return result
  } catch (error) {
    console.error(`Error adding user: ${error}`)
  }
}

async function updateTask(obj) {
  try {
    const response = await fetch(`${url}/${obj.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ ...obj }),
    })
    const updatedTask = await response.json()
    return updatedTask
  } catch (error) {
    console.error(`Error updating user: ${error}`)
  }
}

async function deleteTask(id) {
  try {
    const response = await fetch(`${url}/${id}`, {
      method: 'DELETE',
    })
    const deleted = await response.json()
    return deleted
  } catch (error) {
    console.error(`Error deleting user: ${error}`)
  }
}

export { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
