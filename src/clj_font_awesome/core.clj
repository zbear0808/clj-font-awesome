(ns clj-font-awesome.core
  "FontAwesome 7 icon library for JavaFX/cljfx applications.
   
   Provides cljfx-compatible component functions for rendering
   FontAwesome icons in JavaFX applications built with cljfx.
   
   Basic usage:
     {:fx/type fa/icon
      :name :gear
      :size 16}
   
   With styling:
     {:fx/type fa/icon
      :name :heart
      :style :regular
      :size 24
      :color :red
      :style-class \"my-icon\"}"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clj-font-awesome.fonts :as fonts]))

(def ^:private icon-map
  "Icon definitions loaded from EDN resource."
  (delay
    (-> (io/resource "clj-font-awesome/icons.edn")
        slurp
        edn/read-string)))

(def fallback-unicode
  "Unicode for circle-question icon (used when icon not found)."
  "\uf059")

(defn unicode!
  "Get unicode character for an icon, with fallback.
   Returns fallback-unicode if icon not found.
   
   Examples:
     (unicode! :gear :solid)   ; => \"\\uf013\"
     (unicode! :github :brands); => \"\\uf09b\"
     (unicode! :nonexistent :solid) ; => \"\\uf059\" (circle-question)"
  [icon-name style]
  (get-in @icon-map [style icon-name :unicode] fallback-unicode))

(defn icon
  "Render a FontAwesome icon as a cljfx Label component.
   
   Props:
     :name        (required) Keyword icon name, e.g. :folder-open
     :style       :solid (default), :regular, or :brands
     :size        Font size in pixels (default: 16)
     :color       Text fill color (CSS value, keyword, or JavaFX Paint)
     :style-class Additional CSS class(es) - string or vector of strings
   
   Any additional props are passed through to the underlying Label component.
   
   Returns: cljfx Label description map
   
   Examples:
     {:fx/type icon :name :gear :size 24}
     {:fx/type icon :name :heart :style :regular :color :red}
     {:fx/type icon :name :github :style :brands :size 32}
     {:fx/type icon :name :gear :on-mouse-clicked handler}"
  [{:keys [name style size color style-class]
    :or {style :solid size 16}}]
  (cond-> {:fx/type :label
           :text (unicode! name style)
           :font (fonts/font style size)}
    style-class (assoc :style-class style-class)
    color (assoc :text-fill color)))
