package com.rapido.util;

  /* @author
     ____                        _   _   ____  _             _
	|  _ \ _ __ __ _  __ _  __ _| |_(_) / ___|(_)_ __   __ _| |__
	| |_) | '__/ _` |/ _` |/ _` | __| | \___ \| | '_ \ / _` | '_ \
	|  __/| | | (_| | (_| | (_| | |_| |  ___) | | | | | (_| | | | |
	|_|   |_|  \__,_|\__, |\__,_|\__|_| |____/|_|_| |_|\__, |_| |_|
                     |___/                             |___/
 *
 *  Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.annotation.NonNull;
import android.util.Log;

public class RapidoLog{

    @NonNull
    private static String safe_tag = "incedo";
    private static boolean showLog = true;

    public static void d(String text) {
        if (showLog)
            Log.d("debug : " + safe_tag, "  --  " + text);
    }

    public static void e(String text) {
        if (showLog)
            Log.e("error : " + safe_tag, "  --  " + text);
    }

    public static void v(String text) {
        if (showLog)
            Log.v("verbose : " + safe_tag, "  --  " + text);
    }

    public static void i(String safe_tags, String text) {
        if (showLog)
            Log.i("info : " + safe_tag, "  --  " + text);
    }

    public static void w(String text) {
        if (showLog)
            Log.w("" + safe_tag, " --  " + text);
    }
}