/*
 * Copyright (C) 2015-2022 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onegravity.rteditor.effects;

import com.onegravity.rteditor.fonts.RTTypeface;
import com.onegravity.rteditor.spans.RTSpan;
import com.onegravity.rteditor.spans.TypefaceSpan;

/**
 * Typeface / Fonts.
 */
public class TypefaceEffect extends CharacterEffect<RTTypeface, TypefaceSpan> {

    /**
     * @return If the value is Null then return Null -> remove all TypefaceSpan.
     */
    @Override
    protected RTSpan<RTTypeface> newSpan(RTTypeface value) {
        return value == null ? null : new TypefaceSpan(value);
    }

}
