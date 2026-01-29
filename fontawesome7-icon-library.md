# FontAwesome 7 Icon Library for cljfx

## Why Build Our Own?

1. **No FA7 Support Yet** - Ikonli and other libraries don't support FontAwesome 7
2. **Full Control** - No dependency on third-party library update cycles
4. **CSS Compatible** - Works with existing `-fx-text-fill` styling
5. **Lightweight** - Just font files + lookup map, no heavy dependencies

## Technical Decisions

### Text vs SVG Rendering

**Text rendering chosen because:**
- Lightweight nodes (vs heavy SVG DOM structure)
- Native CSS support (`-fx-text-fill`, `-fx-font-size`)


### Font Variants Required

All three FontAwesome 7 font variants:
- `fa-solid-900.ttf` - Solid icons (most common)
- `fa-regular-400.ttf` - Outlined/hollow variants
- `fa-brands-400.ttf` - Brand logos (GitHub, Twitter, etc.)

### Icon Map Generation

**Build-time generation** (not runtime parsing):
- Parse `icons.json` at build time
- Generate `fa7-icon-map.edn` resource file


---

## Architecture

### File Structure

some of these filenames don't line up exactly 
```
otfs/
    fa-solid-900.ttf
    fa-regular-400.ttf
    fa-brands-400.ttf
  fa7-icon-map.edn          # Generated at build time

scripts/
  generate_fa7_icons.clj    # Build script to parse icons.json


```

### Icon Map Format (fa7-icon-map.edn)

```clojure
{:solid
 {:folder-open {:unicode "\uf07c" :label "Folder Open"}
  :gear        {:unicode "\uf013" :label "Gear"}
  :play        {:unicode "\uf04b" :label "Play"}
  ;; ... all solid icons
  }
 
 :regular
 {:folder-open {:unicode "\uf07c" :label "Folder Open"}
  :heart       {:unicode "\uf004" :label "Heart"}
  ;; ... all regular icons
  }
 
 :brands
 {:github      {:unicode "\uf09b" :label "GitHub"}
  :twitter     {:unicode "\uf099" :label "Twitter"}
  ;; ... all brand icons
  }}
```

### Core Namespace (fa7_icons.clj)

```clojure
(ns laser-show.views.components.fa7-icons
  "FontAwesome 7 icon library for cljfx.
   
   Renders icons as text glyphs for optimal performance.
   Supports solid, regular, and brands font variants."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import [javafx.scene.text Font]))

;; Font loading (lazy, cached)
(def ^:private fonts
  (delay
    {:solid   (Font/loadFont (io/input-stream (io/resource "fonts/fa-solid-900.ttf")) 16)
     :regular (Font/loadFont (io/input-stream (io/resource "fonts/fa-regular-400.ttf")) 16)
     :brands  (Font/loadFont (io/input-stream (io/resource "fonts/fa-brands-400.ttf")) 16)}))

;; Icon map (loaded once at startup)
(def ^:private icon-map
  (delay
    (-> (io/resource "fa7-icon-map.edn")
        slurp
        edn/read-string)))

(defn get-unicode
  "Get unicode character for icon name and style."
  [icon-name style]
  (get-in @icon-map [style icon-name :unicode]))

(defn icon
  "Render a FontAwesome 7 icon.
   
   Props:
   - :name        (required) Keyword icon name (e.g., :folder-open, :gear)
   - :style       (optional) :solid (default), :regular, or :brands
   - :size        (optional) Font size in pixels (default: 16)
   - :style-class (optional) Additional CSS classes"
  [{:keys [name style size style-class]
    :or {style :solid size 16}}]
  (let [font-obj (get @fonts style)
        unicode  (or (get-unicode name style) "\uf059")] ; fallback to question mark
    {:fx/type :label
     :text unicode
     :font (Font. (.getName font-obj) (double size))
     :style-class (cond-> ["icon" (str "icon-" (clojure.core/name style))]
                    style-class (conj style-class))}))
```

### Build Script (generate_fa7_icons.clj)

```clojure
(ns scripts.generate-fa7-icons
  "Build script to generate fa7-icon-map.edn from FontAwesome icons.json"
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn hex->unicode
  "Convert hex string to unicode character."
  [hex-str]
  (str (char (Integer/parseInt hex-str 16))))

(defn parse-icons-json
  "Parse FontAwesome icons.json and return icon map by style."
  [json-path]
  (let [data (json/read-str (slurp json-path) :key-fn keyword)]
    (reduce
      (fn [acc [icon-name info]]
        (let [unicode (hex->unicode (:unicode info))
              styles  (set (:styles info))]
          (cond-> acc
            (contains? styles "solid")
            (assoc-in [:solid (keyword icon-name)]
                      {:unicode unicode :label (:label info)})
            
            (contains? styles "regular")
            (assoc-in [:regular (keyword icon-name)]
                      {:unicode unicode :label (:label info)})
            
            (contains? styles "brands")
            (assoc-in [:brands (keyword icon-name)]
                      {:unicode unicode :label (:label info)}))))
      {:solid {} :regular {} :brands {}}
      data)))

(defn generate!
  "Generate fa7-icon-map.edn from icons.json"
  [icons-json-path output-path]
  (let [icon-map (parse-icons-json icons-json-path)]
    (spit output-path (pr-str icon-map))
    (println "Generated" output-path "with"
             (+ (count (:solid icon-map))
                (count (:regular icon-map))
                (count (:brands icon-map)))
             "icons")))

;; Usage: clj -X scripts.generate-fa7-icons/generate! :icons-json-path '"path/to/icons.json"' :output-path '"resources/fa7-icon-map.edn"'
```

---

## API Design

### Primary API

```clojure
;; Basic usage
{:fx/type fa7-icons/icon
 :name :folder-open}

;; With size
{:fx/type fa7-icons/icon
 :name :gear
 :size 24}

;; Different style (regular = outlined)
{:fx/type fa7-icons/icon
 :name :heart
 :style :regular
 :size 16}

;; Brand icons
{:fx/type fa7-icons/icon
 :name :github
 :style :brands
 :size 20}

;; With custom CSS class
{:fx/type fa7-icons/icon
 :name :check
 :style-class "icon-success"}
```

## CSS Styling


### Updated CSS for FA7

```clojure
{".icon"
 {:-fx-text-fill (:text-primary theme/semantic-colors)}
 
 ".icon-solid"
 {:-fx-font-family "\"Font Awesome 7 Free Solid\""}
 
 ".icon-regular"
 {:-fx-font-family "\"Font Awesome 7 Free Regular\""}
 
 ".icon-brands"
 {:-fx-font-family "\"Font Awesome 7 Brands\""}

 ".button:hover .icon"
 {:-fx-text-fill (:interactive-hover theme/semantic-colors)}
 
 ;; Semantic icon colors
 ".icon-success" {:-fx-text-fill :green}
 ".icon-warning" {:-fx-text-fill :orange}
 ".icon-error"   {:-fx-text-fill :red}
 ".icon-info"    {:-fx-text-fill :blue}}
```

---


---

## Dependencies

### Add to deps.edn (for build script only)

```clojure
;; Only needed for build script, not runtime
:generate-icons
{:extra-deps {org.clojure/data.json {:mvn/version "2.5.0"}}
 :main-opts ["-m" "scripts.generate-fa7-icons"]}
```

---

## Testing Strategy

### Unit Tests
- [ ] Test icon map loading
- [ ] Test unicode lookup for all styles
- [ ] Test fallback behavior for missing icons
