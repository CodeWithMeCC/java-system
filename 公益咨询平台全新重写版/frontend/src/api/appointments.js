import request from './request'
export const getAppointments = () => request.get('/appointments')
export const createAppointment = data => request.post('/appointments', data)
export const cancelAppointment = id => request.post(`/appointments/${id}/cancel`)
export const decideAppointment = (id, data) => request.post(`/appointments/${id}/decision`, data)
