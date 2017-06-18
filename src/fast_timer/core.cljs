(ns fast-timer.core
  (:require [monet.canvas :as canvas]
            [monet.core :as monet]
            [clojure.data :refer [diff]]))

(enable-console-print!)

(defn on-js-reload [])
(defonce app-state (atom {:height (.-innerHeight js/window)
                          :width (.-innerWidth js/window)
                          :pixel-size 5
                          :start (.now js/Date)
                          :end (+ 118000 (.now js/Date))
                          :filled-pixels 0}))

(defn pixel-size [state]
  (:pixel-size @state))

(defn start [state]
  (:start @state))

(defn end [state]
  (:end @state))

(defn pixel-width [state]
  (.floor js/Math (/ (:width @state) (pixel-size state))))

(defn pixel-height [state]
  (.floor js/Math (/ (:height @state) (pixel-size state))))

(defn set-filled-pixels [n]
  (swap! app-state #(merge % {:filled-pixels n})))

(def canvas-dom (.getElementById js/document "canvas"))
(def canvas-ctx (canvas/get-context canvas-dom "2d"))
;(def monet-canvas (canvas/init canvas-dom "2d"))

(defn resize []
  (let [height (.-innerHeight js/window)
        width (.-innerWidth js/window)]
    (print height width)
    (aset canvas-dom "height" height)
    (aset canvas-dom "width" width)
    (swap! app-state #(merge % {:height height
                                :width width}))))


(.addEventListener js/window "resize" resize)
(resize)

(defn random-color []
  (str "#" (.toString (rand-int 16rFFFFFF) 16)))

(defn pixel [ctx vals color]
  (-> ctx
      (canvas/stroke-width 1.0)
      (canvas/stroke-rect vals)
      (canvas/fill-style color)
      (canvas/fill-rect vals)))

(println (random-color))

(defn fill-pixels
  ([color] (fill-pixels color
                        (* (pixel-height app-state) (pixel-width app-state))))
  ([color n]
   (let [px-size (pixel-size app-state)]
     (dotimes [p n]
       (let [row (.floor js/Math (/ p (pixel-width app-state)))
             col (mod p (pixel-width app-state))]
         (pixel canvas-ctx
                {:x (* px-size col)
                 :y (* px-size row)
                 :w px-size
                 :h px-size}
                color))))))

(fill-pixels "#FFFF00")

(defn tick []
  (let [total-time-ms (- (end app-state) (start app-state))
        time-passed-ms (- (.now js/Date) (start app-state))
        total-pixels (* (pixel-width app-state) (pixel-height app-state))
        pixels-per-ms (/ total-pixels total-time-ms)
        filled-pixels (* time-passed-ms pixels-per-ms)]
    (fill-pixels "#FF0000" filled-pixels)
    (set-filled-pixels filled-pixels)))


(defn game-loop []
  (tick)
  (monet/animation-frame game-loop))

(game-loop)


