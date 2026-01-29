(ns clj-font-awesome.cljfx
  "cljfx component helpers for FontAwesome icons.
   
   This namespace provides cljfx-compatible component functions for rendering
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
      :style-class \"my-icon\"}
   
   Icon button:
     {:fx/type fa/icon-button
      :icon-name :trash
      :icon-size 14
      :on-action {:event/type ::delete}}"
  (:require [clj-font-awesome.core :as core]
            [clj-font-awesome.fonts :as fonts]))

(defn icon
  "Render a FontAwesome icon as a cljfx Label component.
   
   Props:
     :name        (required) Keyword icon name, e.g. :folder-open
     :style       :solid (default), :regular, or :brands  
     :size        Font size in pixels (default: 16)
     :color       Text fill color (CSS value, keyword, or JavaFX Paint)
     :style-class Additional CSS class(es) - string or vector of strings
     :opacity     Opacity 0.0-1.0
     :rotate      Rotation angle in degrees
     :tooltip     Tooltip text to show on hover
   
   Returns: cljfx Label description map
   
   Examples:
     {:fx/type icon :name :gear :size 24}
     {:fx/type icon :name :heart :style :regular :color :red}
     {:fx/type icon :name :github :style :brands :size 32}"
  [{:keys [name style size color style-class opacity rotate tooltip]
    :or {style :solid size 16}}]
  (let [unicode-char (core/unicode! name style)
        base-classes ["fa-icon" (str "fa-icon-" (clojure.core/name style))]
        all-classes (cond-> base-classes
                      style-class (into (if (string? style-class)
                                          [style-class]
                                          style-class)))]
    (cond-> {:fx/type :label
             :text unicode-char
             :font (fonts/font style size)
             :style-class all-classes}
      color   (assoc :text-fill color)
      opacity (assoc :opacity opacity)
      rotate  (assoc :rotate rotate)
      tooltip (assoc :tooltip {:fx/type :tooltip :text tooltip}))))


(comment
  ;; Example cljfx usage:
  
  (require '[cljfx.api :as fx])
  
  ;; Simple icon
  {:fx/type icon
   :name :gear
   :size 16}
  
  ;; Icon with color
  {:fx/type icon
   :name :heart
   :style :solid
   :size 24
   :color "#ff0000"}
  
  ;; Brand icon
  {:fx/type icon
   :name :github
   :style :brands
   :size 32}
  )
