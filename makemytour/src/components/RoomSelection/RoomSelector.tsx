import React, { useState, useEffect } from 'react';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Bed, Wifi, Car, Coffee, Bath, Eye, MapPin } from 'lucide-react';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface Room {
  roomNumber: string;
  floor: string;
  available: boolean;
  view: string;
  features: { [key: string]: any };
  bookedBy?: string;
}

interface RoomType {
  type: string;
  description: string;
  amenities: string[];
  basePrice: number;
  rooms: Room[];
  previewImage: string;
  images3D: string[];
}

interface RoomLayout {
  id: string;
  hotelId: string;
  roomTypes: RoomType[];
}

interface RoomSelectorProps {
  hotelId: string;
  userId?: string;
  onRoomSelect?: (roomNumber: string, roomType: string, price: number) => void;
}

const RoomSelector: React.FC<RoomSelectorProps> = ({ hotelId, userId, onRoomSelect }) => {
  const [roomLayout, setRoomLayout] = useState<RoomLayout | null>(null);
  const [selectedRoomType, setSelectedRoomType] = useState<string>('');
  const [selectedRoom, setSelectedRoom] = useState<string>('');
  const [show3DPreview, setShow3DPreview] = useState(false);
  const [current3DImage, setCurrent3DImage] = useState(0);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchRoomLayout();
  }, [hotelId]);

  const fetchRoomLayout = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/room-selection/hotel/${hotelId}`);
      setRoomLayout(response.data);
      if (response.data.roomTypes.length > 0) {
        setSelectedRoomType(response.data.roomTypes[0].type);
      }
    } catch (error) {
      console.error('Error fetching room layout:', error);
    }
  };

  const bookRoom = async (roomNumber: string) => {
    if (!userId) return;

    setLoading(true);
    try {
      await axios.post(`${API_BASE_URL}/api/room-selection/book-room`, {
        hotelId,
        roomNumber,
        userId
      });
      
      // Refresh room layout
      fetchRoomLayout();
      setSelectedRoom('');
    } catch (error) {
      console.error('Error booking room:', error);
    } finally {
      setLoading(false);
    }
  };

  const getAmenityIcon = (amenity: string) => {
    const iconMap: { [key: string]: React.ReactNode } = {
      'WiFi': <Wifi className="h-4 w-4" />,
      'AC': <div className="h-4 w-4 text-blue-500">❄️</div>,
      'TV': <div className="h-4 w-4">📺</div>,
      'Mini Bar': <Coffee className="h-4 w-4" />,
      'Coffee Machine': <Coffee className="h-4 w-4" />,
      'Balcony': <div className="h-4 w-4">🏠</div>,
      'Jacuzzi': <Bath className="h-4 w-4" />,
      'Butler Service': <div className="h-4 w-4">🤵</div>,
      'Smart TV': <div className="h-4 w-4">📱</div>,
    };
    return iconMap[amenity] || <div className="h-4 w-4">✓</div>;
  };

  const getViewIcon = (view: string) => {
    switch (view) {
      case 'ocean':
        return '🌊';
      case 'city':
        return '🏙️';
      case 'garden':
        return '🌳';
      default:
        return '🏨';
    }
  };

  const getRoomStatusColor = (room: Room) => {
    if (!room.available || (room.bookedBy && room.bookedBy !== userId)) {
      return 'bg-gray-200 border-gray-300 cursor-not-allowed';
    }
    if (selectedRoom === room.roomNumber) {
      return 'bg-blue-500 border-blue-600 text-white';
    }
    return 'bg-white border-gray-300 hover:border-blue-400 cursor-pointer';
  };

  if (!roomLayout) {
    return <div className="flex justify-center p-8">Loading room layout...</div>;
  }

  const currentRoomType = roomLayout.roomTypes.find(rt => rt.type === selectedRoomType);

  return (
    <div className="space-y-6">
      {/* Room type selection */}
      <Tabs value={selectedRoomType} onValueChange={setSelectedRoomType}>
        <TabsList className="grid w-full grid-cols-3">
          {roomLayout.roomTypes.map((roomType) => (
            <TabsTrigger key={roomType.type} value={roomType.type} className="capitalize">
              {roomType.type}
            </TabsTrigger>
          ))}
        </TabsList>

        {roomLayout.roomTypes.map((roomType) => (
          <TabsContent key={roomType.type} value={roomType.type} className="space-y-4">
            {/* Room type info */}
            <div className="bg-white p-6 rounded-lg border">
              <div className="flex gap-6">
                <img
                  src={roomType.previewImage}
                  alt={`${roomType.type} room`}
                  className="w-48 h-32 object-cover rounded-lg"
                />
                <div className="flex-1">
                  <h3 className="text-xl font-semibold capitalize mb-2">{roomType.type} Room</h3>
                  <p className="text-gray-600 mb-4">{roomType.description}</p>
                  <div className="flex items-center gap-2 mb-4">
                    <span className="text-2xl font-bold text-blue-600">
                      ${roomType.basePrice}
                    </span>
                    <span className="text-gray-600">per night</span>
                  </div>
                  
                  {/* Amenities */}
                  <div className="flex flex-wrap gap-2">
                    {roomType.amenities.map((amenity) => (
                      <div key={amenity} className="flex items-center gap-1 bg-gray-100 px-2 py-1 rounded text-sm">
                        {getAmenityIcon(amenity)}
                        <span>{amenity}</span>
                      </div>
                    ))}
                  </div>
                </div>
                
                <div className="flex flex-col gap-2">
                  <Dialog open={show3DPreview} onOpenChange={setShow3DPreview}>
                    <DialogTrigger asChild>
                      <Button variant="outline" size="sm">
                        <Eye className="h-4 w-4 mr-2" />
                        3D Preview
                      </Button>
                    </DialogTrigger>
                    <DialogContent className="sm:max-w-2xl">
                      <DialogHeader>
                        <DialogTitle>3D Room Preview - {roomType.type}</DialogTitle>
                      </DialogHeader>
                      <div className="space-y-4">
                        <img
                          src={roomType.images3D[current3DImage]}
                          alt={`3D view ${current3DImage + 1}`}
                          className="w-full h-80 object-cover rounded-lg"
                        />
                        <div className="flex justify-center gap-2">
                          {roomType.images3D.map((_, index) => (
                            <button
                              key={index}
                              onClick={() => setCurrent3DImage(index)}
                              className={`w-3 h-3 rounded-full ${
                                current3DImage === index ? 'bg-blue-600' : 'bg-gray-300'
                              }`}
                            />
                          ))}
                        </div>
                      </div>
                    </DialogContent>
                  </Dialog>
                </div>
              </div>
            </div>

            {/* Available rooms */}
            <div>
              <h4 className="text-lg font-semibold mb-4">Available Rooms</h4>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {roomType.rooms
                  .filter(room => room.available || room.bookedBy === userId)
                  .map((room) => (
                    <div
                      key={room.roomNumber}
                      className={`p-4 rounded-lg border-2 transition-all ${getRoomStatusColor(room)}`}
                      onClick={() => {
                        if (room.available && (!room.bookedBy || room.bookedBy === userId)) {
                          setSelectedRoom(selectedRoom === room.roomNumber ? '' : room.roomNumber);
                          if (onRoomSelect) {
                            onRoomSelect(room.roomNumber, roomType.type, roomType.basePrice);
                          }
                        }
                      }}
                    >
                      <div className="flex items-center justify-between mb-2">
                        <span className="font-semibold">Room {room.roomNumber}</span>
                        <span className="text-sm text-gray-600">Floor {room.floor}</span>
                      </div>
                      
                      <div className="flex items-center gap-2 mb-2">
                        <MapPin className="h-4 w-4 text-gray-500" />
                        <span className="text-sm capitalize">{room.view} view</span>
                        <span className="text-lg">{getViewIcon(room.view)}</span>
                      </div>
                      
                      <div className="space-y-1 text-sm text-gray-600">
                        {Object.entries(room.features).map(([feature, value]) => (
                          <div key={feature} className="flex justify-between">
                            <span className="capitalize">{feature.replace(/([A-Z])/g, ' $1')}:</span>
                            <span>{typeof value === 'boolean' ? (value ? 'Yes' : 'No') : value}</span>
                          </div>
                        ))}
                      </div>
                      
                      {room.bookedBy === userId && (
                        <div className="mt-2 text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
                          Your Booking
                        </div>
                      )}
                    </div>
                  ))}
              </div>
              
              {roomType.rooms.filter(room => room.available).length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  No rooms available in this category
                </div>
              )}
            </div>
          </TabsContent>
        ))}
      </Tabs>

      {/* Selection summary */}
      {selectedRoom && currentRoomType && (
        <div className="bg-blue-50 p-4 rounded-lg">
          <div className="flex items-center justify-between">
            <div>
              <p className="font-medium">
                Selected: {currentRoomType.type} Room {selectedRoom}
              </p>
              <p className="text-sm text-gray-600">
                ${currentRoomType.basePrice} per night
              </p>
            </div>
            
            {userId && (
              <Button 
                onClick={() => bookRoom(selectedRoom)} 
                disabled={loading}
              >
                {loading ? 'Booking...' : 'Book This Room'}
              </Button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomSelector;