/*
 * Copyright (C) 2012 Sergej Dechand <cryptocall@serj.de>
 *                    Dominik Schürmann <dominik@dominikschuermann.de>
 * 
 * This file is part of CryptoCall.
 * 
 * CryptoCall is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CryptoCall is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CryptoCall.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
 
package org.cryptocall.service;

import org.cryptocall.service.handler.ICryptoCallGetPublicKeyHandler;

/**
 * All methods are oneway, which means they are asynchronous and non-blocking.
 * Results are returned into given Handler, which has to be implemented on client side.
 */
interface ICryptoCallService {

    oneway void getPublicKey(in String telephoneNumber, in ICryptoCallGetPublicKeyHandler handler);

}