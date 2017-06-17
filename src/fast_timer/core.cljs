(ns fast-timer.core
  (:require [monet.canvas :as canvas]))

(enable-console-print!)

(defn on-js-reload [])
(defonce app-state (atom {:text "Hello world!"
                          :height (.-innerHeight js/window)
                          :width (.-innerWidth js/window)}))


(def canvas-dom (.getElementById js/document "canvas"))

(defn resize []
  (aset canvas-dom "height" (:height @app-state))
  (aset canvas-dom "width" (:width @app-state))
  (swap! app-state #(merge % {:height (.-innerHeight js/window)
                              :width  (.-innerWidth js/window)})))
(.addEventListener js/window "resize" resize)

(resize)

(def monet-canvas (canvas/init canvas-dom "2d"))


(canvas/add-entity monet-canvas :background
                   (canvas/entity {:x 0 :y 0 :w (:width @app-state) :h (:height @app-state)} ; val
                                  (fn []
                                    {:x 0 :y 0 :w (:width @app-state) :h (:height @app-state)})
                                  (fn [ctx val]             ; draw function
                                    (-> ctx
                                        (canvas/fill-style "#191d21")
                                        (canvas/fill-rect val)))))

(defn move-around [& args]
  {:x (* 1000 (.random js/Math))  :y (* 1000 (.random js/Math))  :w 20 :h 20})


(canvas/add-entity monet-canvas :box
                   (canvas/entity {:x 0 :y 0 :w 20 :h 20} ; val
                                  move-around                       ; update function
                                  (fn [ctx val]             ; draw function
                                    (-> ctx
                                        (canvas/fill-style "#FFFFFF")
                                        (canvas/fill-rect val)))))

;(canvas/start-updating monet-canvas)