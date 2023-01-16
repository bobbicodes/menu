(ns menu.app
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]))

(defonce !files (r/atom []))
(defonce !bg (r/atom nil))

(defn logo [x]
  [:g
   #_[:rect {:x x :y 20
             :width 530 :height 500
             :rx 25 :fill "#B0AFAB90"}]
   [:image {:href "img\\happy-hemp-trans.svg"
            :x (+ x 20)
            :y 30
            :width 500}]])

(defn svg-width [id]
  (.-width (.getBBox (.getElementById js/document id))))

(defn svg-height [id]
  (.-height (.getBBox (.getElementById js/document id))))

(def label-width (r/atom 1000))
(def label-height (r/atom 100))

(defonce label-str (r/atom "CBD Tinctures"))

(defn label [s]
  [:g
   [:rect {:x      (- (/ 3840 2) (/ @label-width 1.9))
           :y      (/ @label-height 10)
           :width  (* 1.05 @label-width)
           :height (* @label-height 0.85)
           :rx     65
           :fill   "#977F4760"}]
   [:text#label
    {:x           (- (/ 3840 2) (/ @label-width 2))
     :y           (* @label-height 0.75)
     :font-weight 1000
     :font-family "Brush Script MT"
     :fill        "white"
     :font-size   256}
    s]
   [:text
    {:x           (- (/ 3840 2) (/ @label-width 2))
     :y           (* @label-height 0.75)
     :font-family "Brush Script MT"
     :fill        "green"
     :font-size   256}
    s]])

(defn svg-string [el]
  (let [serializer (js/XMLSerializer.)]
    (.serializeToString serializer (.getElementById js/document el))))

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

(defn app []
  [:div#app
   {:style {:background-image (str "url(" @!bg ")")}}
   [:svg#svg-bg {:xmlns "http://www.w3.org/2000/svg"
                 :width    "100%"
                 :view-box "0 0 3840 2160"
                 :on-load (fn [_]
                            (reset! label-width (svg-width "label"))
                            (reset! label-height (svg-height "label")))}
    [:image {:href @!bg}]
    [logo 0]
    [logo 3300]
    [label @label-str]]])

(defn draw-svg [id]
  (let [canvas   (.getElementById js/document "canvas")
        ctx      (.getContext canvas "2d")
        img      (js/Image.)
        svg      (js/Blob. [(svg-string "svg-bg")] (clj->js {:type "image/svg+xml;charset=utf-8"}))
        url      (js/URL.createObjectURL svg)]
    (set! (.-src img) url)
    (set! (.-onload img)
            #(do (set! (.-globalCompositeOperation ctx) "source-over")
                 (.drawImage ctx img 0 0)))))

(defn render-canvas [bg composite]
  (let [canvas   (.getElementById js/document "canvas")
        ctx      (.getContext canvas "2d")
        svg      (js/Blob. [(svg-string "svg-bg")] (clj->js {:type "image/svg+xml;charset=utf-8"}))
        url      (js/URL.createObjectURL svg)]
    (.clearRect ctx 0 0 3840 2160)
    (draw bg "source-over")
    (draw-logo "img\\happy-hemp-trans.svg" composite)
    (draw-svg "svg-bg")
    url))

(comment
  (reset! label-str "CBD Tinctures")
  (reset! !bg "img\\flower\\bg-flower.png")
  (reset! !bg "img\\tinctures\\bg-tinctures.png")
  (reset! !bg "img\\topicals\\bg-topicals.png")
  (render-canvas "img\\bg.jpg" "source-over")
  (render-canvas "img\\flower\\bg-flower.png" "difference")
  (render-canvas "img\\flower\\bg-flower.png" "source-over")
  (render-canvas "img\\tinctures\\bg-tinctures.png" "source-over")
  (render-canvas "img\\topicals\\bg-topicals.png" "source-over"))

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