(ns menu.app
  (:require [reagent.dom :as rdom]))

(defn draw [url composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        img    (js/Image.)
        _      (set! (.-src img) url)
        _      (set! (.-onload img)
                     #(do (set! (.-globalCompositeOperation ctx) composite)
                          (.drawImage ctx img 0 0)))]))

(defn draw-logo [url composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        img    (js/Image.)
        _      (set! (.-src img) url)
        _      (set! (.-onload img)
                     #(do (set! (.-globalCompositeOperation ctx) composite)
                          (.drawImage ctx img 0 0 500 500)
                          (.drawImage ctx img 3300 0 500 500)
                          (.drawImage ctx img 0 1650 500 500)
                          (.drawImage ctx img 3300 1650 500 500)))]))

(defn roundedRect [ctx x y width height radius color]
  (set! (.-fillStyle ctx) color)
  (.beginPath ctx)
  (.moveTo ctx x (+ y radius))
  (.arcTo ctx x (+ y height)  (+ x radius) (+ y height)radius)
  (.arcTo ctx (+ x width) (+ y height) (+ x width) (- (+ y height) radius) radius)
  (.arcTo ctx (+ x width) y (- (+ x width) radius) y radius)
  (.arcTo ctx x y x (+ y radius) radius)
  (.fill ctx))

(defn render-canvas [bg label composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        width  (* 100 (count label))]
    (.clearRect ctx 0 0 3840 2160)
    (set! (.-globalCompositeOperation ctx) "destination-over")
    (roundedRect ctx (- (/ 3840 2) (/ width 1.9)) 50 
                     (* 1.05 width) 240
                     25 "#977F4760")
    (set! (.-font ctx) "256px Brush Script MT")
    (set! (.-fillStyle ctx) "green")
    (.fillText ctx label  (- (/ 3840 2) (/ width 2)) 240
                ;; restrict text size to 100px/character
               ;; https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/fillText#restricting_the_text_size
               width)
    (draw-logo "img\\happy-hemp-trans.svg" composite)
    (draw bg "destination-over")))

(defn app []
  [:div#app
 ;  [render-canvas "img\\flower\\bg-flower.png" "CBD Flower" "source-over"]
 ;  [render-canvas "img\\flower\\bg-flower.png" "CBD Flower" "difference"]
   [render-canvas "img\\bg.jpg" "Happy Hemp Farmacy" "source-over"]
;  [render-canvas "img\\tinctures\\bg-tinctures.png" "CBD Tinctures" "source-over"]
;   [render-canvas "img\\topicals\\bg-topicals.png" "CBD Topicals" "source-over"]
   ])

(defn render []
  (rdom/render [app]
               (.getElementById js/document "root")))

(defn ^:dev/after-load start []
  (render)
  (js/console.log "start"))

(defn ^:export init []
  (js/console.log "init")
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "stop"))