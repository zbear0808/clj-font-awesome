(ns clj-font-awesome.fonts
  "Font loading utilities for FontAwesome 7."
  (:require [clojure.java.io :as io])
  (:import [javafx.scene.text Font]))

(def ^:private font-resources
  {:solid   "clj-font-awesome/fonts/fa7-solid.otf"
   :regular "clj-font-awesome/fonts/fa7-regular.otf"
   :brands  "clj-font-awesome/fonts/fa7-brands.otf"})

(defn- load-font [style size]
  (let [resource-path (get font-resources style)]
   (cond (not (get font-resources style))
        (throw (ex-info (str "Unknown font style: " style) {}))
        
        (not (io/resource resource-path))
        (throw (ex-info (str "Font resource not found: " resource-path) {}))
        
        :else
        (with-open [stream (io/input-stream (io/resource resource-path))]
          (Font/loadFont stream (double size))))))

(def ^:private base-fonts
  (delay
    (update-vals
     font-resources
     #(load-font % 16.0))))

(def ^:private font-cache
  (atom {}))

(defn font
  "Get a cached Font instance for the given style and size."
  [style size]
  (let [cache-key [style size]
        font (if (contains? @font-cache cache-key)
               (get @font-cache cache-key)
               (load-font style size))]
    (swap! font-cache assoc cache-key font)
    font))
