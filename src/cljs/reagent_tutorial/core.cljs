(ns reagent-tutorial.core
  (:require
    [cljs.reader :refer [read-string]]
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [ajax.core :refer [GET]]
    [goog.events.KeyCodes :as keycodes]
    [goog.events :as gev])
  (:import [goog.events EventType KeyHandler]))

; TODO: Replace this with a "selector" for `my-slides`
(def max-val 100)

; Movement functions & state
(def current-elem (r/atom 0))

(defn move-forwards []
  (swap! current-elem (fn [v]
                       (if (= v (dec max-val))
                         v
                         (inc v)))))

(defn move-backwards []
  (swap! current-elem (fn [v]
                       (if (= v 0)
                         v
                         (dec v)))))

(defn reset-current-elem []
  (reset! current-elem 0))

; Slide controls
(defn forwards-button []
  [:input {:type "button"
           :value ">>"
           :style {:display "inline"}
           :on-click move-forwards}])

(defn backwards-button []
  [:input {:type "button"
           :value "<<"
           :style {:display "inline"}
           :on-click move-backwards}])

(defn reset-button []
  [:input {:type "button"
           :value "Reset"
           :style {:display "inline"}
           :on-click reset-current-elem}])

(defn controls []
  [:div {:id "controls-container"}
   [backwards-button]
   [forwards-button]
   [reset-button]])

; Slide components & "builders"
(defn slide-component [i inner-vec]
  [:div {:key i
         :id (str "slide-" i)
         :style {:display (if (= @current-elem i) "" "none")}}
   inner-vec])

(defn slide-builder [slides]
  (->> slides
    (map-indexed (fn [i slide]
                   ^{:key i} [slide-component i slide]))))

(defn fetch-slides! [data]
  (GET "/slides"
     {:handler #(reset! data (read-string %))
      :error-handler (fn [{:keys [status status-text]}]
                       (js/console.log status status-text))}))

(defn main-component []
  (let [my-slides (r/atom nil)]
    (fetch-slides! my-slides)
    (fn []
      [:div
       [:div {:id "slides-container"}
         (slide-builder @my-slides)]
       [controls]])))

(def keycode-map
  {keycodes/LEFT  move-backwards
   keycodes/RIGHT move-forwards
   keycodes/H     move-backwards
   keycodes/L     move-forwards
   keycodes/SPACE move-forwards
   keycodes/ESC   reset-current-elem})

(defn on-keydown [e]
  (.preventDefault e)
  (when-let [f (get keycode-map (.. e -keyCode))]
    (f)))

(defn mount-root []
  (rdom/render [main-component] (.getElementById js/document "app")))

(defn init! []
  (mount-root)
  (gev/listen js/document "keydown" on-keydown))

(comment
  (read-string (pr-str [1 2 3]))
  (let [foo (r/atom nil)]

    (fetch-slides! foo)
    (println @foo))
  )
