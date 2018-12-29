package ru.prolib.bootes.tsgr001a.mscan;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MSCANScannerImpl implements MSCANScanner {
	
	static class EventNode {
		private final MSCANEvent event;
		private final MSCANSensor sensor;
		
		EventNode(MSCANEvent event, MSCANSensor sensor) {
			this.event = event;
			this.sensor = sensor;
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != EventNode.class ) {
				return false;
			}
			EventNode o = (EventNode) other;
			return new EqualsBuilder()
					.append(o.event, event)
					.append(o.sensor, sensor)
					.build();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(117293, 301)
					.append(event)
					.append(sensor)
					.build();
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

	}
	
	private final Set<MSCANSensor> sensors;
	private final Set<MSCANListener> listeners;
	private final Set<EventNode> events;
	
	MSCANScannerImpl(Set<MSCANSensor> startingSensors,
			Set<MSCANListener> listeners,
			Set<EventNode> events)
	{
		this.sensors = startingSensors;
		this.listeners = listeners;
		this.events = events;
	}
	
	public MSCANScannerImpl() {
		this(new HashSet<>(), new HashSet<>(), new HashSet<>());
	}

	@Override
	public synchronized void addSensor(MSCANSensor sensor) {
		sensors.add(sensor);
	}
	
	@Override
	public synchronized void removeSensor(MSCANSensor sensor) {
		sensors.remove(sensor);
	}

	@Override
	public synchronized void addListener(MSCANListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeListener(MSCANListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized void analyze(Instant currentTime) {
		Iterator<EventNode> it = events.iterator();
		while ( it.hasNext() ) {
			EventNode n = it.next();
			MSCANLogEntry entry = n.sensor.analyze(currentTime, n.event);
			if ( entry != null ) {
				if ( entry.isTrigger() ) {
					it.remove();
					for ( MSCANListener listener : listeners ) {
						listener.onEventClosed(n.event);
					}
				} else {
					for ( MSCANListener listener : listeners ) {
						listener.onEventChanged(n.event, entry);
					}
				}
			}
		}
		
		for ( MSCANSensor sensor : sensors ) {
			MSCANEvent event = sensor.analyze(currentTime);
			if ( event != null ) {
				if ( event.isFinished() ) {
					for ( MSCANListener listener : listeners ) {
						listener.onEventSkipped(event);
					}
				} else {
					events.add(new EventNode(event, sensor));
					for ( MSCANListener listener : listeners ) {
						listener.onEventStarted(event);
					}
				}
			}
		}
	}

}
