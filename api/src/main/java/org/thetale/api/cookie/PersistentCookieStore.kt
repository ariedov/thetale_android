/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Lukas Zorich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.thetale.api.cookie


import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson

import java.net.CookieManager
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

/**
 * Repository for cookies. CookieManager will store cookies of every incoming HTTP response into
 * CookieStore, and retrieve cookies for every outgoing HTTP request.
 *
 *
 * Cookies are stored in [android.content.SharedPreferences] and will persist on the
 * user's device between application session. [com.google.gson.Gson] is used to serialize
 * the cookies into a json string in order to be able to save the cookie to
 * [android.content.SharedPreferences]
 *
 *
 * Created by lukas on 17-11-14.
 */
class PersistentCookieStore(private val context: Context) : CookieStore {

    private val store: CookieStore = CookieManager().cookieStore

    private val jsonSessionCookieString: String
        get() = prefs.getString(PREF_SESSION_COOKIE, PREF_DEFAULT_STRING)

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        // get the default in memory store and if there is a cookie stored in shared preferences,
        // we added it to the cookie store
        val jsonSessionCookie = jsonSessionCookieString
        if (jsonSessionCookie != PREF_DEFAULT_STRING) {
            val gson = Gson()
            val cookie = gson.fromJson(jsonSessionCookie, HttpCookie::class.java)
            store.add(URI.create(cookie.domain), cookie)
        }
    }

    override fun add(uri: URI, cookie: HttpCookie) {
        if (cookie.name == "sessionid") {
            // if the cookie that the cookie store attempt to add is a session cookie,
            // we remove the older cookie and save the new one in shared preferences
            remove(URI.create(cookie.domain), cookie)
            saveSessionCookie(cookie)
        }

        store.add(URI.create(cookie.domain), cookie)
    }

    override fun get(uri: URI): List<HttpCookie> {
        return store.get(uri)
    }

    override fun getCookies(): List<HttpCookie> {
        return store.cookies
    }

    override fun getURIs(): List<URI> {
        return store.urIs
    }

    override fun remove(uri: URI, cookie: HttpCookie): Boolean {
        return store.remove(uri, cookie)
    }

    override fun removeAll(): Boolean {
        return store.removeAll()
    }

    /**
     * Saves the HttpCookie to SharedPreferences as a json string.
     *
     * @param cookie The cookie to save in SharedPreferences.
     */
    private fun saveSessionCookie(cookie: HttpCookie) {
        val gson = Gson()
        val jsonSessionCookieString = gson.toJson(cookie)
        val editor = prefs.edit()
        editor.putString(PREF_SESSION_COOKIE, jsonSessionCookieString)
        editor.apply()
    }

    companion object {

        /**
         * The default preferences string.
         */
        private const val PREF_DEFAULT_STRING = ""

        /**
         * The preferences name.
         */
        private val PREFS_NAME = PersistentCookieStore::class.java.name

        /**
         * The preferences session cookie key.
         */
        private const val PREF_SESSION_COOKIE = "session_cookie"
    }
}