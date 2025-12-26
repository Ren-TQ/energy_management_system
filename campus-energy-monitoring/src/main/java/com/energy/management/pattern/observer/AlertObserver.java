package com.energy.management.pattern.observer;

import com.energy.management.entity.Alert;

public interface AlertObserver {
    
    void onAlertTriggered(Alert alert);
    
    String getObserverName();
}

