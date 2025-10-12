import React, { useState, useEffect } from 'react';
import { SimpleLineChart } from '../ui/simple-chart';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { TrendingUp, TrendingDown, Minus, Clock, DollarSign } from 'lucide-react';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface PricePoint {
  timestamp: string;
  price: number;
  reason: string;
}

interface PriceHistory {
  id: string;
  itemId: string;
  itemType: string;
  pricePoints: PricePoint[];
  currentPrice: number;
  basePrice: number;
  demandMultiplier: number;
  lastUpdated: string;
}

interface PriceInsights {
  trend: string;
  recommendation: string;
  averagePrice: number;
  currentPrice: number;
  priceHistory: PricePoint[];
}

interface PriceTrackerProps {
  itemId: string;
  itemType: 'flight' | 'hotel';
  travelDate: string;
  userId?: string;
}

const PriceTracker: React.FC<PriceTrackerProps> = ({ itemId, itemType, travelDate, userId }) => {
  const [priceHistory, setPriceHistory] = useState<PriceHistory | null>(null);
  const [priceInsights, setPriceInsights] = useState<PriceInsights | null>(null);
  const [currentPrice, setCurrentPrice] = useState<number>(0);
  const [showPriceHistory, setShowPriceHistory] = useState(false);
  const [priceFreezed, setPriceFreezed] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchCurrentPrice();
    fetchPriceHistory();
    fetchPriceInsights();
  }, [itemId, itemType, travelDate]);

  const fetchCurrentPrice = async () => {
    try {
      const response = await axios.post(`${API_BASE_URL}/api/pricing/calculate`, {
        itemId,
        itemType,
        travelDate
      });
      setCurrentPrice(response.data.dynamicPrice);
    } catch (error) {
      console.error('Error fetching current price:', error);
    }
  };

  const fetchPriceHistory = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/pricing/history/${itemId}/${itemType}`);
      setPriceHistory(response.data);
    } catch (error) {
      console.error('Error fetching price history:', error);
    }
  };

  const fetchPriceInsights = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/api/pricing/insights/${itemId}/${itemType}`);
      setPriceInsights(response.data);
    } catch (error) {
      console.error('Error fetching price insights:', error);
    }
  };

  const freezePrice = async (hours: number = 24) => {
    if (!userId) return;

    setLoading(true);
    try {
      const response = await axios.post(`${API_BASE_URL}/api/pricing/freeze`, {
        itemId,
        itemType,
        userId,
        hours
      });
      
      if (response.data.success) {
        setPriceFreezed(true);
        setTimeout(() => setPriceFreezed(false), hours * 60 * 60 * 1000);
      }
    } catch (error) {
      console.error('Error freezing price:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTrendIcon = (trend: string) => {
    switch (trend) {
      case 'increasing':
        return <TrendingUp className="h-5 w-5 text-red-500" />;
      case 'decreasing':
        return <TrendingDown className="h-5 w-5 text-green-500" />;
      default:
        return <Minus className="h-5 w-5 text-gray-500" />;
    }
  };

  const getTrendColor = (trend: string) => {
    switch (trend) {
      case 'increasing':
        return 'text-red-600 bg-red-50';
      case 'decreasing':
        return 'text-green-600 bg-green-50';
      default:
        return 'text-gray-600 bg-gray-50';
    }
  };

  const formatChartData = (pricePoints: PricePoint[]) => {
    return pricePoints.map((point, index) => ({
      name: `Day ${index + 1}`,
      price: point.price,
      date: new Date(point.timestamp).toLocaleDateString(),
      reason: point.reason
    }));
  };

  return (
    <div className="space-y-4">
      {/* Current price display */}
      <div className="bg-white p-6 rounded-lg border">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold mb-2">Current Price</h3>
            <div className="flex items-center gap-2">
              <span className="text-3xl font-bold text-blue-600">
                ${currentPrice.toFixed(2)}
              </span>
              {priceFreezed && (
                <div className="flex items-center gap-1 bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm">
                  <Clock className="h-4 w-4" />
                  Price Frozen
                </div>
              )}
            </div>
            
            {priceInsights && (
              <div className={`flex items-center gap-2 mt-2 px-3 py-1 rounded-full text-sm ${getTrendColor(priceInsights.trend)}`}>
                {getTrendIcon(priceInsights.trend)}
                <span className="capitalize">{priceInsights.trend}</span>
              </div>
            )}
          </div>
          
          <div className="flex flex-col gap-2">
            {userId && !priceFreezed && (
              <Button onClick={() => freezePrice(24)} disabled={loading} size="sm">
                <DollarSign className="h-4 w-4 mr-2" />
                {loading ? 'Freezing...' : 'Freeze Price'}
              </Button>
            )}
            
            <Dialog open={showPriceHistory} onOpenChange={setShowPriceHistory}>
              <DialogTrigger asChild>
                <Button variant="outline" size="sm">
                  View History
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-2xl">
                <DialogHeader>
                  <DialogTitle>Price History</DialogTitle>
                </DialogHeader>
                {priceHistory && priceHistory.pricePoints && priceHistory.pricePoints.length > 0 ? (
                  <div className="space-y-4">
                    <SimpleLineChart 
                      data={formatChartData(priceHistory.pricePoints)}
                      height={300}
                    />
                    
                    <div className="grid grid-cols-2 gap-4 text-sm">
                      <div>
                        <span className="font-medium">Base Price:</span>
                        <span className="ml-2">${priceHistory.basePrice.toFixed(2)}</span>
                      </div>
                      <div>
                        <span className="font-medium">Current Price:</span>
                        <span className="ml-2">${priceHistory.currentPrice.toFixed(2)}</span>
                      </div>
                      <div>
                        <span className="font-medium">Demand Multiplier:</span>
                        <span className="ml-2">{priceHistory.demandMultiplier.toFixed(2)}x</span>
                      </div>
                      <div>
                        <span className="font-medium">Last Updated:</span>
                        <span className="ml-2">
                          {new Date(priceHistory.lastUpdated).toLocaleString()}
                        </span>
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-8">
                    <TrendingUp className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <p className="text-gray-600">No price history available yet</p>
                    <p className="text-sm text-gray-500 mt-2">Price tracking data will appear here once available</p>
                  </div>
                )}
              </DialogContent>
            </Dialog>
          </div>
        </div>
      </div>

      {/* Price insights */}
      {priceInsights && (
        <div className="bg-gray-50 p-4 rounded-lg">
          <h4 className="font-medium mb-2">Price Insights</h4>
          <p className="text-sm text-gray-700 mb-2">{priceInsights.recommendation}</p>
          
          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-600">Average Price:</span>
              <span className="ml-2 font-medium">${priceInsights.averagePrice.toFixed(2)}</span>
            </div>
            <div>
              <span className="text-gray-600">Current vs Average:</span>
              <span className={`ml-2 font-medium ${
                priceInsights.currentPrice < priceInsights.averagePrice 
                  ? 'text-green-600' 
                  : 'text-red-600'
              }`}>
                {priceInsights.currentPrice < priceInsights.averagePrice ? '-' : '+'}
                ${Math.abs(priceInsights.currentPrice - priceInsights.averagePrice).toFixed(2)}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Price freeze info */}
      {priceFreezed && (
        <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
          <div className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-blue-600" />
            <div>
              <p className="font-medium text-blue-800">Price Frozen!</p>
              <p className="text-sm text-blue-700">
                Your price is locked at ${currentPrice.toFixed(2)} for the next 24 hours.
                Complete your booking to secure this rate.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PriceTracker;