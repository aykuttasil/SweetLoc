/* Author - Aykut Asil(@aykuttasil) */
package com.aykuttasil.sweetloc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aykuttasil.sweetloc.data.UserModel
import java.text.DateFormat
import java.util.Date

@Entity(tableName = "user")
data class UserEntity(
  @PrimaryKey var userId: String,
  var userEmail: String? = null,
  var userPassword: String? = null,
  var userName: String? = null,
  var userSurname: String? = null,
  var userTel: String? = null,
  var userRegId: String? = null,
  var userToken: String? = null,
  var userImageUrl: String? = null,
  var userOneSignalId: String? = null,
  var userCity: String? = null,
  var userCreateDate: Long? = Date().time,
  var userCreateDateString: String? = DateFormat.getInstance().format(Date()),
  var userLastLoginDate: Long? = Date().time
)

fun UserEntity.toUserModel(): UserModel {
    val userModel = UserModel()
    userModel.userId = this.userId
    userModel.userName = this.userName
    userModel.userEmail = this.userEmail
    return userModel
}
