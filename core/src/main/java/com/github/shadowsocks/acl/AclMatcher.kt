/*******************************************************************************
 *                                                                             *
 *  Copyright (C) 2019 by Max Lv <max.c.lv@gmail.com>                          *
 *  Copyright (C) 2019 by Mygod Studio <contact-shadowsocks-android@mygod.be>  *
 *                                                                             *
 *  This program is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by       *
 *  the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                        *
 *                                                                             *
 *  This program is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 *  GNU General Public License for more details.                               *
 *                                                                             *
 *  You should have received a copy of the GNU General Public License          *
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                             *
 *******************************************************************************/

package com.github.shadowsocks.acl

import com.github.shadowsocks.net.Subnet
import java.net.Inet4Address
import java.net.Inet6Address

class AclMatcher(id: String) {
    private val subnetsIpv4: List<Subnet.Immutable>
    private val subnetsIpv6: List<Subnet.Immutable>
    private val bypassDomains = mutableListOf<Regex>()
    private val proxyDomains = mutableListOf<Regex>()
    private val bypass: Boolean

    init {
        val (bypass, subnets) = Acl.parse(Acl.getFile(id).bufferedReader(), {
//            bypassDomains.add(it.toRegex())
        }, {
            if (it.startsWith("(?:^|\\.)googleapis")) proxyDomains.add(it.toRegex())
        })
        subnetsIpv4 = subnets.filter { it.address is Inet4Address }.map { it.toImmutable() }
        subnetsIpv6 = subnets.filter { it.address is Inet6Address }.map { it.toImmutable() }
        this.bypass = bypass
    }

    fun shouldBypassIpv4(ip: ByteArray) = bypass xor subnetsIpv4.any { it.matches(ip) }
    fun shouldBypassIpv6(ip: ByteArray) = bypass xor subnetsIpv6.any { it.matches(ip) }
    fun shouldBypass(host: String): Boolean? {
        if (bypassDomains.any { it.matches(host) }) return true
        if (proxyDomains.any { it.matches(host) }) return false
        return null
    }
}