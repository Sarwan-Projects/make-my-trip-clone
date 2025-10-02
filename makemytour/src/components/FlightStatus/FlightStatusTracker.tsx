import React, { useState, useEffect } from 'react';
import { Plane, Clock, AlertTriangle, CheckCircle, XCircle } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface FlightStatus {
  id: string;
  flightId: string;
  flightNumber: string;
  status: string;
  delayReason?: string;
  delayMinutes: number;
  scheduledDeparture: string;
  actualDeparture?: string;
  estimatedDeparture?: string;
  scheduledArrival: string;
  actualArrival?: string;
  estimatedArrival?: string;
  gate?: string;
  terminal?: string;
  lastUpdated: string;
}

interface FlightStatusTrackerProps {
  userFlights?: string[];
}

const FlightStatusTracker: React.FC<FlightStatusTrackerProps> = ({ userFlights = [] }) => {
  const [flightStatuses, setFlightStatuses] = useState<FlightStatus[]>([]);
  const [searchFlightNumber, setSearchFlightNumber] = useState('');
  const [searchResult, setSearchResult] = useState<FlightStatus | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (userFlights.length > 0) {
      fetchUserFlightStatuses();
    }
  }, [userFlights]);

  const fetchUserFlightStatuses = async () => {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/flight-status/user-flights`, {
        flightIds: userFlights
      });
      setFlightStatuses(response.data);
    } catch (error) {
      console.error('Error fetching flight statuses:', error);
    }
  };

  const searchFlightStatus = async () => {
    if (!searchFlightNumber.trim()) return;

    setLoading(true);
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/flight-status/by-number/${searchFlightNumber}`
      );
      setSearchResult(response.data);
    } catch (error) {
      console.error('Error searching flight status:', error);
      setSearchResult(null);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'on-time':
        return <CheckCircle className="h-5 w-5 text-green-600" />;
      case 'delayed':
        return <Clock className="h-5 w-5 text-yellow-600" />;
      case 'cancelled':
        return <XCircle className="h-5 w-5 text-red-600" />;
      case 'boarding':
        return <Plane className="h-5 w-5 text-blue-600" />;
      case 'departed':
        return <Plane className="h-5 w-5 text-green-600" />;
      case 'arrived':
        return <CheckCircle className="h-5 w-5 text-green-600" />;
      default:
        return <Clock className="h-5 w-5 text-gray-600" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'on-time':
      case 'departed':
      case 'arrived':
        return 'text-green-600 bg-green-50';
      case 'delayed':
        return 'text-yellow-600 bg-yellow-50';
      case 'cancelled':
        return 'text-red-600 bg-red-50';
      case 'boarding':
        return 'text-blue-600 bg-blue-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  const formatTime = (dateString: string) => {
    return new Date(dateString).toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric'
    });
  };

  const FlightStatusCard: React.FC<{ flight: FlightStatus }> = ({ flight }) => (
    <div className="border rounded-lg p-4 space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Plane className="h-6 w-6 text-blue-600" />
          <div>
            <h3 className="font-semibold">{flight.flightNumber}</h3>
            <p className="text-sm text-gray-600">
              {formatDate(flight.scheduledDeparture)}
            </p>
          </div>
        </div>
        
        <div className={`flex items-center gap-2 px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(flight.status)}`}>
          {getStatusIcon(flight.status)}
          {flight.status.charAt(0).toUpperCase() + flight.status.slice(1)}
          {flight.delayMinutes > 0 && (
            <span>({flight.delayMinutes}min)</span>
          )}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-sm font-medium">Departure</p>
          <p className="text-lg">
            {flight.actualDeparture 
              ? formatTime(flight.actualDeparture)
              : flight.estimatedDeparture 
                ? formatTime(flight.estimatedDeparture)
                : formatTime(flight.scheduledDeparture)
            }
          </p>
          {flight.gate && (
            <p className="text-sm text-gray-600">Gate {flight.gate}</p>
          )}
        </div>
        
        <div>
          <p className="text-sm font-medium">Arrival</p>
          <p className="text-lg">
            {flight.actualArrival 
              ? formatTime(flight.actualArrival)
              : flight.estimatedArrival 
                ? formatTime(flight.estimatedArrival)
                : formatTime(flight.scheduledArrival)
            }
          </p>
          {flight.terminal && (
            <p className="text-sm text-gray-600">Terminal {flight.terminal}</p>
          )}
        </div>
      </div>

      {flight.delayReason && (
        <div className="flex items-start gap-2 p-3 bg-yellow-50 rounded-lg">
          <AlertTriangle className="h-4 w-4 text-yellow-600 mt-0.5" />
          <div>
            <p className="text-sm font-medium text-yellow-800">Delay Reason</p>
            <p className="text-sm text-yellow-700">{flight.delayReason}</p>
          </div>
        </div>
      )}

      <div className="text-xs text-gray-500">
        Last updated: {new Date(flight.lastUpdated).toLocaleString()}
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Search flight status */}
      <div className="bg-white p-4 rounded-lg border">
        <h2 className="text-lg font-semibold mb-4">Check Flight Status</h2>
        <div className="flex gap-2">
          <Input
            type="text"
            placeholder="Enter flight number (e.g., AI101)"
            value={searchFlightNumber}
            onChange={(e) => setSearchFlightNumber(e.target.value.toUpperCase())}
            className="flex-1"
            onKeyPress={(e) => e.key === 'Enter' && searchFlightStatus()}
          />
          <Button onClick={searchFlightStatus} disabled={loading}>
            {loading ? 'Searching...' : 'Search'}
          </Button>
        </div>
        
        {searchResult && (
          <div className="mt-4">
            <FlightStatusCard flight={searchResult} />
          </div>
        )}
      </div>

      {/* User's flights */}
      {flightStatuses.length > 0 && (
        <div>
          <h2 className="text-lg font-semibold mb-4">Your Flights</h2>
          <div className="space-y-4">
            {flightStatuses.map((flight) => (
              <FlightStatusCard key={flight.id} flight={flight} />
            ))}
          </div>
        </div>
      )}

      {/* Live updates notification */}
      <div className="bg-blue-50 p-4 rounded-lg">
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 bg-blue-600 rounded-full animate-pulse"></div>
          <p className="text-sm text-blue-800">
            Flight statuses are updated in real-time. You'll receive notifications for any changes.
          </p>
        </div>
      </div>
    </div>
  );
};

export default FlightStatusTracker;