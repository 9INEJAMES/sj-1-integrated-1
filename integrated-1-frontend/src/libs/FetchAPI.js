const url = import.meta.env.VITE_BASE_STATUS_URL

async function getAllStatuses() {
  try {
    const data = await fetch(`${url}`)
    const result = await data.json()
    return result
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function getStatusById(id) {
  try {
    const data = await fetch(`${url}/${id}`)
    const result = await data.json()
    return result
  } catch (error) {
    console.log(`error: ${error}`)
  }
}

async function addStatus(obj) {
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

async function updateStatus(obj) {
  try {
    const response = await fetch(`${url}/${obj.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ ...obj }),
    })
    const updatedStatus = await response.json()
    return updatedStatus
  } catch (error) {
    console.error(`Error updating user: ${error}`)
  }
}

async function deleteStatus(id) {
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

export { getAllStatuses, getStatusById, addStatus, updateStatus, deleteStatus }
