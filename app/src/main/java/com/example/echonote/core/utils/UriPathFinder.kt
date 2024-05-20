package com.example.echonote.core.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.lang.Long

class UriPathFinder {
    fun getPath(context: Context, uri: Uri): String? {
        try {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        if (split.size >= 2) {
                            val type = split[0]
                            if ("primary".equals(type, ignoreCase = true)) {
                                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                            }
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri =
                            ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                id.toLongOrNull() ?: -1
                            )
                        return getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":").toTypedArray()
                        if (split.size >= 2) {
                            val type = split[0]
                            var contentUri: Uri? = null
                            when (type) {
                                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                            }
                            val selection = "_id=?"
                            val selectionArgs = arrayOf(split[1])
                            return getDataColumn(context, contentUri, selection, selectionArgs)
                        }
                    }
                }
            } else if ("content" == uri.scheme) {
                return getDataColumn(context, uri, null, null)
            } else if ("file" == uri.scheme) {
                return uri.path
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error getting path from URI: $uri", e)
        }
        return null
    }



    fun getP(context: Context, uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).toTypedArray()
        val type = split[0]
        var contentUri: Uri? = null
        when (type) {
            "image" -> {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            "video" -> {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            "audio" -> {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            "document" -> {

            }
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getDataColumn(context, contentUri, selection, selectionArgs)
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            if (uri != null) {

                cursor =
                    context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            }
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

}