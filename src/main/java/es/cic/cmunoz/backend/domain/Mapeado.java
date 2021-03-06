/*
 * Copyright 2017 cmunoz.
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
package es.cic.cmunoz.backend.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mapeado {

    private Map<String, String> mapaCadenas;

    public Mapeado() {
        mapaCadenas = new HashMap<>();
    }

    public Map<String, String> getMapaCadenas() {
        return mapaCadenas;
    }

    public void setMapaCadenas(Map<String, String> mapaCadenas) {
        this.mapaCadenas = mapaCadenas;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.mapaCadenas);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Mapeado other = (Mapeado) obj;
        if (!Objects.equals(this.mapaCadenas, other.mapaCadenas)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String cadenatoString = "";
        for (String cadenaSacada : this.mapaCadenas.values()) {
            cadenatoString = cadenatoString.concat(cadenaSacada);
        }

        return cadenatoString;
    }

}
