(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]))

(defn draw 
  ([ctx url] (draw ctx url 0 0 "source-over"))
  ([ctx url composite]
   (draw ctx url 0 0 composite))
  ([ctx url x y composite]
   (let [img    (js/Image.)
         _      (set! (.-src img) url)
         _      (set! (.-onload img)
                      #(do (set! (.-globalCompositeOperation ctx) composite)
                           (.drawImage ctx img x y)))]))
  ([ctx url x y width height]
   (draw ctx url x y width height "source-over")) 
  ([ctx url x y width height composite]
    (let [img    (js/Image.)
          _      (set! (.-src img) url)
          _      (set! (.-onload img)
                       #(do (set! (.-globalCompositeOperation ctx) composite)
                            (.drawImage ctx img x y width height)))])))

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

(defn label [ctx x y width height text color]
  (set! (.-globalCompositeOperation ctx) "source-over")
  (roundedRect ctx x y width height 45 "#977F47a0")
  ;(roundedRect ctx x y 500 (* 1.5 height) 45 "#977F4750")
  (set! (.-font ctx) "114px Brush Script MT")
  (set! (.-shadowColor ctx) "white")
  (set! (.-shadowOffsetX ctx) 10)
   (set! (.-shadowOffsetY ctx) 10)
  (set! (.-fillStyle ctx) color)
  (.fillText ctx text (+ x 30) (+ y 90))
  ctx)

(defn render-prices [ctx x y]
  (let [x1 x
        y1 (+ y 1000)
        x2 (+ x 480)
        y2 (+ y 1285)
        x3 (+ x 1000)
        y3 (+ y 1130)
        y4 (+ y 1410)
        h  120
        w1 350
        w2 350]
    
    (roundedRect ctx x (+ y 1000) 1350 530 45 "#977F4760")
    ;(draw ctx "img\\tinctures\\bg-tinctures.png")
    (label ctx x1 y1 w1 h "500mg" "green")
    (label ctx x2 y1 w2 h "1000mg" "green")
    (label ctx x3 y1 w2 h "1500mg" "green")
    (label ctx x1 y2 w1 h "500mg" "green")
    (label ctx x2 y2 w2 h "1000mg" "green")
    (label ctx x3 y2 w2 h "1500mg" "green")
    (label ctx x1 y3 w1 h " $28" "black")
    (label ctx x2 y3 w1 h " $38" "black")
    (label ctx x3 y3 w1 h " $65" "black")
    (label ctx x1 y4 w2 h "$22.50" "black")
    (label ctx x2 y4 w1 h " $35" "black")
    (label ctx x3 y4 w1 h " $58" "black")))

(defn millis
  "Get the current time in milliseconds."
  []
  (.getTime (js/Date.)))

#_(defn render-frame []
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        img    (js/Image.)
        _  (.clearRect ctx 0 0 3840 2160)
        _      (set! (.-src img) "img\\tinctures\\bg-tinctures.png")
        _      (set! (.-onload img)
                     #(do (set! (.-globalCompositeOperation ctx) "destination-over")
                          (.drawImage ctx img 0 0)))]
   
    (render-prices (.getContext (.getElementById js/document "canvas") "2d")
                   (- (mod (millis) (+ 1400 3840)) 1400) 0)
    (.requestAnimationFrame js/window render-frame)))

(defn render-frame []
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")]
    (draw ctx "img\\tinctures\\bg-tinctures.png")
    (render-prices (.getContext (.getElementById js/document "canvas") "2d")
                   (- (mod (millis) (+ 1400 3840)) 1400) 0)
    (.requestAnimationFrame js/window render-frame)))

(render-frame)

(defn render-canvas [bg label composite]
  (let [canvas (.getElementById js/document "canvas")
        ctx    (.getContext canvas "2d")
        width  (* 100 (count label))]
    (.clearRect ctx 0 0 3840 2160)
    ; (draw ctx bg)
    #_(roundedRect ctx (- (/ 3840 2) (/ width 1.9)) 50 
                     (* 1.05 width) 240
                     25 "#977F4760")
    ;(set! (.-font ctx) "256px Brush Script MT")
    ;(set! (.-fillStyle ctx) "green")
    #_(.fillText ctx label  (- (/ 3840 2) (/ width 2)) 240
                ;; restrict text size to 100px/character
               ;; https://developer.mozilla.org/en-US/docs/Web/API/CanvasRenderingContext2D/fillText#restricting_the_text_size
               width)
    
    ;(draw-logo "img\\happy-hemp-trans.svg" composite)
    ;(set! (.-globalCompositeOperation ctx) "destination-over")
    ;(roundedRect ctx 380 500 1000 1000 45 "#977F4760")
   ; (draw "img\\tinctures\\refills-trans.png" 380 500 1000 1000 "source-over")
    ))

(defn app []
  [:div#app
 ;  [render-canvas "img\\flower\\bg-flower.png" "CBD Flower" "source-over"]
 ;  [render-canvas "img\\flower\\bg-flower.png" "CBD Flower" "difference"]
  ; [render-canvas "img\\bg.jpg" "Happy Hemp Farmacy"]
  [render-canvas "img\\tinctures\\bg-tinctures.png" "CBD Tinctures" "source-over"]
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