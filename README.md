[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.zubad/clj-font-awesome.svg)](https://clojars.org/org.clojars.zubad/clj-font-awesome)

FontAwesome 7 icons for cljfx/JavaFX.

## deps.edn

```clojure
org.clojars.zubad/clj-font-awesome {:mvn/version "RELEASE"}
```

## Usage

`:name` (kebab-cased keyword) is required. Defaults: `:style :solid`, `:size 16`. Optional: `:color`, `:style-class`.

```clojure
(require '[clj-font-awesome.core :as fa])

;; Basic icon
{:fx/type fa/icon
 :name :gear
 :size 16}

;; Styled icon
{:fx/type fa/icon
 :name :heart
 :style :regular
 :size 24
 :color :red}
```
