import React, { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../ui/dialog';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface Seat {
  seatNumber: string;
  type: string;
  available: boolean;
  isWindow: boolean;
  isAisle: boolean;
  extraPrice: number;
  bookedBy?: string;
}

interface SeatRow {
  rowNumber: string;
  seats: Seat[];
}

interface SeatMapData {
  id: string;
  flightId: string;
  aircraftType: string;
  seatRows: SeatRow[];
  seatPricing: { [key: string]: number };
}

interface SeatMapProps {
  flightId: string;
  userId?: string;
  onSeatSelect?: (seats: string[], totalPrice: number) => void;
}

const SeatMap: React.FC<SeatMapProps> = ({ flightId, userId, onSeatSelect }) => {
  const [seatMap, setSeatMap] = useState<SeatMapData | null>(null);
  const [selectedSeats, setSelectedSeats] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [showPricing, setShowPricing] = useState(false);

  useEffect(() => {
    fetchSeatMap();
  }, [flightId]);

  const fetchSeatMap = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/seat-selection/flight/${flightId}`);
      setSeatMap(response.data);
    } catch (error) {
      console.error('Error fetching seat map:', error);
    }
  };

  const handleSeatClick = (seat: Seat) => {
    if (!seat.available || (seat.bookedBy && seat.bookedBy !== userId)) {
      return;
    }

    const isSelected = selectedSeats.includes(seat.seatNumber);
    let newSelectedSeats: string[];

    if (isSelected) {
      newSelectedSeats = selectedSeats.filter(s => s !== seat.seatNumber);
    } else {
      newSelectedSeats = [...selectedSeats, seat.seatNumber];
    }

    setSelectedSeats(newSelectedSeats);

    if (onSeatSelect) {
      const totalPrice = calculateTotalPrice(newSelectedSeats);
      onSeatSelect(newSelectedSeats, totalPrice);
    }
  };

  const calculateTotalPrice = (seats: string[]) => {
    if (!seatMap) return 0;

    let total = 0;
    seatMap.seatRows.forEach(row => {
      row.seats.forEach(seat => {
        if (seats.includes(seat.seatNumber)) {
          total += seat.extraPrice;
        }
      });
    });
    return total;
  };

  const getSeatClass = (seat: Seat) => {
    const baseClass = "w-8 h-8 rounded-t-lg border-2 cursor-pointer transition-all duration-200 flex items-center justify-center text-xs font-medium";
    
    if (!seat.available || (seat.bookedBy && seat.bookedBy !== userId)) {
      return `${baseClass} bg-gray-300 border-gray-400 cursor-not-allowed text-gray-600`;
    }

    if (selectedSeats.includes(seat.seatNumber)) {
      return `${baseClass} bg-blue-500 border-blue-600 text-white shadow-lg`;
    }

    switch (seat.type) {
      case 'business':
        return `${baseClass} bg-purple-100 border-purple-300 hover:bg-purple-200 text-purple-800`;
      case 'premium':
        return `${baseClass} bg-yellow-100 border-yellow-300 hover:bg-yellow-200 text-yellow-800`;
      case 'economy':
        return `${baseClass} bg-green-100 border-green-300 hover:bg-green-200 text-green-800`;
      default:
        return `${baseClass} bg-gray-100 border-gray-300 hover:bg-gray-200`;
    }
  };

  const bookSelectedSeats = async () => {
    if (!userId || selectedSeats.length === 0) return;

    setLoading(true);
    try {
      await axios.post(`${API_BASE_URL}/api/seat-selection/book-seats`, {
        flightId,
        seatNumbers: selectedSeats,
        userId
      });
      
      // Refresh seat map
      fetchSeatMap();
      setSelectedSeats([]);
    } catch (error) {
      console.error('Error booking seats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!seatMap) {
    return <div className="flex justify-center p-8">Loading seat map...</div>;
  }

  const totalUpgradePrice = calculateTotalPrice(selectedSeats);

  return (
    <div className="space-y-6">
      {/* Aircraft info */}
      <div className="text-center">
        <h3 className="text-lg font-semibold">{seatMap.aircraftType}</h3>
        <p className="text-sm text-gray-600">Select your preferred seats</p>
      </div>

      {/* Legend */}
      <div className="flex flex-wrap justify-center gap-4 text-sm">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-green-100 border border-green-300 rounded"></div>
          <span>Economy</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-yellow-100 border border-yellow-300 rounded"></div>
          <span>Premium</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-purple-100 border border-purple-300 rounded"></div>
          <span>Business</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-gray-300 border border-gray-400 rounded"></div>
          <span>Occupied</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-blue-500 border border-blue-600 rounded"></div>
          <span>Selected</span>
        </div>
      </div>

      {/* Seat map */}
      <div className="bg-white p-6 rounded-lg border max-w-md mx-auto">
        {/* Cockpit */}
        <div className="text-center mb-4">
          <div className="w-full h-8 bg-gray-200 rounded-t-full flex items-center justify-center text-xs font-medium text-gray-600">
            COCKPIT
          </div>
        </div>

        {/* Seats */}
        <div className="space-y-2">
          {seatMap.seatRows.map((row) => (
            <div key={row.rowNumber} className="flex items-center gap-2">
              <div className="w-6 text-xs font-medium text-gray-600 text-center">
                {row.rowNumber}
              </div>
              
              <div className="flex gap-1">
                {row.seats.slice(0, Math.ceil(row.seats.length / 2)).map((seat) => (
                  <div key={seat.seatNumber} className="relative">
                    <button
                      className={getSeatClass(seat)}
                      onClick={() => handleSeatClick(seat)}
                      title={`${seat.seatNumber} - ${seat.type} ${seat.extraPrice > 0 ? `(+$${seat.extraPrice})` : ''}`}
                    >
                      {seat.seatNumber.slice(-1)}
                    </button>
                    {seat.isWindow && (
                      <div className="absolute -left-1 top-1/2 transform -translate-y-1/2 w-1 h-4 bg-blue-200 rounded-l"></div>
                    )}
                  </div>
                ))}
              </div>

              {/* Aisle */}
              <div className="w-4"></div>

              <div className="flex gap-1">
                {row.seats.slice(Math.ceil(row.seats.length / 2)).map((seat) => (
                  <div key={seat.seatNumber} className="relative">
                    <button
                      className={getSeatClass(seat)}
                      onClick={() => handleSeatClick(seat)}
                      title={`${seat.seatNumber} - ${seat.type} ${seat.extraPrice > 0 ? `(+$${seat.extraPrice})` : ''}`}
                    >
                      {seat.seatNumber.slice(-1)}
                    </button>
                    {seat.isWindow && (
                      <div className="absolute -right-1 top-1/2 transform -translate-y-1/2 w-1 h-4 bg-blue-200 rounded-r"></div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Selection summary */}
      {selectedSeats.length > 0 && (
        <div className="bg-blue-50 p-4 rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium">Selected Seats: {selectedSeats.join(', ')}</p>
              {totalUpgradePrice > 0 && (
                <p className="text-sm text-gray-600">
                  Upgrade fee: ${totalUpgradePrice.toFixed(2)}
                </p>
              )}
            </div>
            
            {userId && (
              <Button onClick={bookSelectedSeats} disabled={loading}>
                {loading ? 'Booking...' : 'Confirm Selection'}
              </Button>
            )}
          </div>
        </div>
      )}

      {/* Pricing info */}
      <Dialog open={showPricing} onOpenChange={setShowPricing}>
        <Button variant="outline" onClick={() => setShowPricing(true)} className="w-full">
          View Seat Pricing
        </Button>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Seat Upgrade Pricing</DialogTitle>
          </DialogHeader>
          <div className="space-y-3">
            {Object.entries(seatMap.seatPricing).map(([type, price]) => (
              <div key={type} className="flex justify-between items-center">
                <span className="capitalize font-medium">{type} Class</span>
                <span className="font-semibold">
                  {price === 0 ? 'Included' : `+$${price.toFixed(2)}`}
                </span>
              </div>
            ))}
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default SeatMap;