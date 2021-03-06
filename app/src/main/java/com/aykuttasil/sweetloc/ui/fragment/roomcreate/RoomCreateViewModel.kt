/* Author - Aykut Asil(@aykuttasil) */
package com.aykuttasil.sweetloc.ui.fragment.roomcreate

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.aykuttasil.sweetloc.data.RoomEntity
import com.aykuttasil.sweetloc.data.repository.RoomRepository
import com.aykuttasil.sweetloc.data.repository.UserRepository
import com.aykuttasil.sweetloc.ui.BaseAndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomCreateViewModel @Inject constructor(
  val app: Application,
  val userRepository: UserRepository,
  val roomRepository: RoomRepository
) : BaseAndroidViewModel(app) {

    val liveRoomName: MutableLiveData<String> = MutableLiveData()

    fun createRoom() {
        launch {
            liveRoomName.value?.let { roomName ->
                val isExistRoomName =
                    withContext(Dispatchers.Default) { roomRepository.isExistRoomName(roomName).await() }
                if (!isExistRoomName) {
                    val user = userRepository.getUserEntitySuspend()
                    val room = RoomEntity(roomName = roomName, roomOwner = user?.userId)
                    val roomId = withContext(Dispatchers.Default) { roomRepository.addRoom(room).await() }

                    withContext(Dispatchers.Default) { roomRepository.addUserRoom(user!!.userId, roomId, room).await() }
                    withContext(Dispatchers.Default) {
                        roomRepository.addRoomMember(user!!.userId, roomId, user).await()
                    }

                    liveSnackbar.value = "Room added."
                } else {
                    liveSnackbar.value = "Room didn't add!"
                }
            }
        }
    }

    fun addMemberToRoom() {
        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(
                Uri.parse(
                    "https://example.page.link/?link=" +
                            "https://www.aykutasil.com/&apn=com.example.android&ibn=com.example.ios"
                )
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                val flowchartLink = result.previewLink
            }
            .addOnFailureListener {
                // Error
                // ...
            }

        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid
        val link = "https://mygame.example.com/?invitedby=$uid"
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(link))
            .setDomainUriPrefix("https://example.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.example.android")
                    .setMinimumVersion(125)
                    .build()
            )
            .setIosParameters(
                DynamicLink.IosParameters.Builder("com.example.ios")
                    .setAppStoreId("123456789")
                    .setMinimumVersion("1.0.1")
                    .build()
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { shortDynamicLink ->
                // mInvitationUrl = shortDynamicLink.shortLink
                // ...
            }
    }
}
