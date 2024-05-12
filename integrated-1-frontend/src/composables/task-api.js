import { useToast } from '@/stores/toast'

export const useTaskApi = () => {
    const myToast = useToast()
    const url = import.meta.env.VITE_BASE_URL

    async function getAllTasks() {
        try {
            const data = await fetch(`${url}/tasks`)
            const result = await data.json()
            return result
        } catch (error) {
            console.log(`error: ${error}`)
        }
    }

    async function getTaskById(id) {
        try {
            const data = await fetch(`${url}/tasks/${id}`)
            const result = await data.json()
            if (data.status / 400 >= 1) {
                myToast.changeToast(false, 'The requested task does not exist')
                // router.push({ name: 'home' })
                return
            }
            return result
        } catch (error) {
            myToast.changeToast(false, 'The requested task does not exist')
            console.log(`error: ${error}`)
        }
    }

    async function addTask(obj) {
        try {
            const response = await fetch(`${url}/tasks`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            const result = await response.json()
            myToast.changeToast(true, 'The task has been successfully added')
            return result
        } catch (error) {
            console.error(`Error adding task: ${error}`)
        }
    }

    async function updateTask(obj) {
        try {
            const response = await fetch(`${url}/tasks/${obj.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ ...obj }),
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'The update was unsuccesful')
                return
            }
            const updatedTask = await response.json()
            myToast.changeToast(true, 'The task has been updated')
            return updatedTask
        } catch (error) {
            myToast.changeToast(false, 'The update was unsuccesful')
            console.error(`Error updating task: ${error}`)
        }
    }

    async function deleteTask(id) {
        try {
            const response = await fetch(`${url}/tasks/${id}`, {
                method: 'DELETE',
            })
            if (response.status / 400 >= 1) {
                myToast.changeToast(false, 'An error has occurred, the task does not exist.')
                return
            }
            const deleted = await response.json()
            myToast.changeToast(true, 'The task has been deleted')
            return deleted
        } catch (error) {
            myToast.changeToast(false, 'An error has occurred, the task does not exist.')
            console.error(`Error deleting task: ${error}`)
        }
    }

    return { getAllTasks, getTaskById, addTask, updateTask, deleteTask }
}
