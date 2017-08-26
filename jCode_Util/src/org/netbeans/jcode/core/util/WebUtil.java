/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.openide.util.Utilities;

/**
 *
 * @author Gaurav Gupta
 */
public class WebUtil {

    public static boolean isValidUri(String uri) {
        StringTokenizer segments = new StringTokenizer(uri, "/ "); //NOI18N
        Set<String> uriParts = new HashSet<String>();
        while (segments.hasMoreTokens()) {
            String segment = segments.nextToken();
            if (segment.startsWith("{")) { //NOI18N
                if (segment.length() > 2 && segment.endsWith("}")) { //NOI18N
                    String uriPart = segment.substring(1, segment.length() - 1);
                    if (!Utilities.isJavaIdentifier(uriPart)) {
                        return false;
                    }
                    if (uriParts.contains(uriPart)) {
                        return false;
                    } else {
                        uriParts.add(uriPart);
                    }

                } else {
                    return false;
                }
            } else {
                if (segment.contains("{") || segment.contains("}")) { //NOI18N
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isInternetReachable(String url) {
        try {

            InetAddress address = InetAddress.getByName(url);

            if (address == null) {
                return false;
            }

        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }

}
