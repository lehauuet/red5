(where (service #"{{service_selector}}")
  #(info "got event: " %)

  (where (not (expired? event))
    (def max_runtime {{stability_time}})
    (def current_runtime (atom 0))
    (moving-time-window {{moving_window_size}}
      (info "++++++++++++TIME WINDOW++++++++++++++")
      (fn [events]
      (info "current_runtime= " @current_runtime "max_runtime= " max_runtime)
        (let [ 
               hostmap (atom {})
               hostcnt (atom {})
             ]
          (do
            (doseq [m events]
              (if (nil? (@hostmap (m :host)))
                (do
                  (swap! hostmap assoc (m :host) (m :metric))
                  (swap! hostcnt assoc (m :host) 1)
                )
                (do
                  (swap! hostmap assoc (m :host) (+ (m :metric) (@hostmap (m :host))))
                  (swap! hostcnt assoc (m :host) (inc (@hostcnt (m :host))))
                )
              )
            )
            (doseq [entry @hostmap]
              (swap! hostmap assoc (key entry) (/ (val entry) (@hostcnt (key entry))))
            )
  
            (let 
              [ hostcnt (count @hostmap)
                conns (/ (apply + (map (fn [a] (val a)) @hostmap)) hostcnt) 
                cooling (not (nil? (riemann.index/lookup index "scaling" "suspended")))]
              (do
                (info "cooling=" cooling " scale_direction=SCALE IN scale_limit=" @scale_limit "hostcnt=" hostcnt " scale_threshold={{scale_threshold}} conns=" conns)
                (if (and (not cooling) ({{scale_direction}} @scale_limit {{scale_limit}}) ({{scale_direction}} {{scale_threshold}} conns))
                  (do
                    (if (< @current_runtime max_runtime)
                      (do
                      	(Thread/sleep 1000) ;Sleep 1 seconds before add current_runtime value up
                        (swap! current_runtime inc)
                      )
                      (do
                        ;(info "current_runtime= " @current_runtime "max_runtime= " max_runtime)
                        (info "=== SCALE ===" "{{scale_direction}}")
                        (reset! scale_limit (- @scale_limit 1))
                        (process-policy-triggers {})
                        (riemann.index/update index {:host "scaling" :service "suspended" :time (unix-time) :description "cooldown flag" :metric 0 :ttl {{cooldown_time}} :state "ok"})
                      )
                    )
                  )
                  ; (do
                  ;   (reset! current_runtime (/ @current_runtime 2))
                  ; )
                  (do
                    (if (= @current_runtime 0)
                      (do
                      )
                      (do
                        (if (odd? @current_runtime)
                          (do
                            (reset! current_runtime (/ (- @current_runtime 1) 2))
                          )
                          (do
                            (reset! current_runtime (/ @current_runtime 2))
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  )
)
