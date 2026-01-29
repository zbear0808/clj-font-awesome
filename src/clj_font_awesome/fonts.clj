(ns clj-font-awesome.fonts
  "Font loading utilities for FontAwesome 7.
   
   This namespace handles loading and caching of FontAwesome font files
   for use in JavaFX applications.
   
   Fonts are loaded lazily on first use and cached by size for efficiency.
   
   Basic usage:
     (font :solid 16)   ; => Font instance for solid icons at 16px
     (font-family :solid) ; => \"Font Awesome 7 Free Solid\""
  (:require [clojure.java.io :as io])
  (:import [javafx.scene.text Font]))

(def ^:private font-resources
  "Resource paths for FontAwesome font files."
  {:solid   "clj-font-awesome/fonts/fa7-solid.otf"
   :regular "clj-font-awesome/fonts/fa7-regular.otf"
   :brands  "clj-font-awesome/fonts/fa7-brands.otf"})

(defn load-font
  "Load a font from resources at the specified size.
   This creates a new Font instance each time - prefer using `font` for caching.
   
   Arguments:
     style - :solid, :regular, or :brands
     size  - font size in pixels
   
   Returns: Font instance
   
   Throws: Exception if font resource not found"
  [style size]
  (let [resource-path (get font-resources style)]
    (when-not resource-path
      (throw (ex-info (str "Unknown font style: " style)
                      {:style style :available-styles (keys font-resources)})))
    (let [resource (io/resource resource-path)]
      (when-not resource
        (throw (ex-info (str "Font resource not found: " resource-path)
                        {:style style :resource-path resource-path})))
      (with-open [stream (io/input-stream resource)]
        (Font/loadFont stream (double size))))))

(def ^:private base-fonts
  "Base font instances loaded at default size for name extraction.
   Lazily loaded on first access."
  (delay
    (into {}
      (for [[style _] font-resources]
        [style (load-font style 16.0)]))))

(def ^:private font-cache
  "Atom containing cached font instances by [style size] key."
  (atom {}))

(defn font
  "Get a cached Font instance for the given style and size.
   
   Arguments:
     style - :solid (default), :regular, or :brands
     size  - font size in pixels (default: 16)
   
   Returns: Font instance (cached)
   
   Examples:
     (font)             ; => Font for :solid at 16px
     (font :solid 24)   ; => Font for :solid at 24px
     (font :brands 16)  ; => Font for :brands at 16px"
  ([] (font :solid 16))
  ([style] (font style 16))
  ([style size]
   (let [cache-key [style size]]
     (if-let [cached (get @font-cache cache-key)]
       cached
       (let [base-font (get @base-fonts style)]
         (if base-font
           (let [new-font (Font. (.getName base-font) (double size))]
             (swap! font-cache assoc cache-key new-font)
             new-font)
           (let [new-font (load-font style size)]
             (swap! font-cache assoc cache-key new-font)
             new-font)))))))

(defn font-family
  "Get the font family name for CSS usage.
   
   Arguments:
     style - :solid (default), :regular, or :brands
   
   Returns: String like \"Font Awesome 7 Free Solid\"
   
   Examples:
     (font-family)         ; => \"Font Awesome 7 Free Solid\"
     (font-family :brands) ; => \"Font Awesome 7 Brands Regular\""
  ([] (font-family :solid))
  ([style]
   (.getFamily (get @base-fonts style))))

(defn font-name
  "Get the full font name (for direct use with JavaFX).
   
   Arguments:
     style - :solid (default), :regular, or :brands
   
   Returns: String - the full font name
   
   Examples:
     (font-name)         ; => \"Font Awesome 7 Free Solid\"
     (font-name :brands) ; => \"Font Awesome 7 Brands Regular\""
  ([] (font-name :solid))
  ([style]
   (.getName (get @base-fonts style))))

(defn clear-cache!
  "Clear the font cache. Useful if you need to free memory.
   Fonts will be reloaded on next access."
  []
  (reset! font-cache {}))

(defn available-styles
  "Get a list of available font styles.
   Returns: [:solid :regular :brands]"
  []
  (vec (keys font-resources)))
