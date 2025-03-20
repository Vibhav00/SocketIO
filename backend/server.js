const express = require('express')
const http = require('http')
const { Server } = require('socket.io')

const app = express()
const server = http.createServer(app)
const io = new Server(server)

const users = new Map() // Stores userId to socket mapping

io.on('connection', (socket) => {
  console.log(`A user connected , user id = ${socket.id} `)

  socket.on('register', (userId) => {
    users.set(userId, socket)
    console.log(`User ${userId} registered`)
  })

  socket.on('sendMessage', ({ userId, message, toAll }) => {
    if (toAll) {
      io.emit('receiveMessage', { userId, message })
    } else {
      console.log(`* ${userId} - ${message} = ${toAll}`)
      const recipientSocket = users.get(userId)
      console.log('Users Map:')
      users.forEach((socket, userId) => {
        console.log(`User ID: ${userId}, Socket ID: ${socket.id}`)
      })

      if (recipientSocket) {
        console.log(`emited .... `)
        recipientSocket.emit('receiveMessage', { userId, message })
      }
    }
  })

  socket.on('disconnect', () => {
    users.forEach((s, id) => {
      if (s === socket) users.delete(id)
    })
    console.log('A user disconnected')
  })
})

server.listen(3000, () => console.log('Server running on port 3000'))
