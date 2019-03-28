package com.aykuttasil.sweetloc.data


import java.text.DateFormat
import java.util.Date

data class Room(
        val roomName: String,
        val createDate: Long = Date().time,
        val createDateString: String = DateFormat.getInstance().format(Date())
)


fun roomsNode() = "rooms"
fun roomNode(roomId: String) = "${roomsNode()}/$roomId"
fun roomMembersNode(roomId: String) = "${roomsNode()}/$roomId/members"


fun usersNode() = "users"
fun userNode(userId: String) = "${usersNode()}/$userId"
fun userRoomsNode(userId: String) = "${usersNode()}/$userId/rooms"
fun userLocationsNode(userId: String) = "${usersNode()}/$userId/locations"

