import React from 'react';

interface DataPoint {
  name: string;
  price: number;
  date: string;
  reason: string;
}

interface SimpleLineChartProps {
  data: DataPoint[];
  width?: string | number;
  height?: number;
}

export const SimpleLineChart: React.FC<SimpleLineChartProps> = ({ 
  data, 
  width = "100%", 
  height = 300 
}) => {
  if (!data || data.length === 0) {
    return <div className="flex items-center justify-center h-64 text-gray-500">No data available</div>;
  }

  const maxPrice = Math.max(...data.map(d => d.price));
  const minPrice = Math.min(...data.map(d => d.price));
  const priceRange = maxPrice - minPrice || 1;

  const svgWidth = typeof width === 'string' ? 600 : width;
  const svgHeight = height;
  const padding = 40;
  const chartWidth = svgWidth - 2 * padding;
  const chartHeight = svgHeight - 2 * padding;

  const points = data.map((point, index) => {
    const x = padding + (index / (data.length - 1)) * chartWidth;
    const y = padding + ((maxPrice - point.price) / priceRange) * chartHeight;
    return { x, y, ...point };
  });

  const pathData = points.map((point, index) => 
    `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`
  ).join(' ');

  return (
    <div className="w-full">
      <svg width={svgWidth} height={svgHeight} className="border rounded">
        {/* Grid lines */}
        <defs>
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#e5e7eb" strokeWidth="1"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#grid)" />
        
        {/* Price line */}
        <path
          d={pathData}
          fill="none"
          stroke="#2563eb"
          strokeWidth="2"
        />
        
        {/* Data points */}
        {points.map((point, index) => (
          <g key={index}>
            <circle
              cx={point.x}
              cy={point.y}
              r="4"
              fill="#2563eb"
              stroke="white"
              strokeWidth="2"
            />
            <title>{`${point.date}: $${point.price} (${point.reason})`}</title>
          </g>
        ))}
        
        {/* Y-axis labels */}
        <text x="10" y={padding} textAnchor="start" className="text-xs fill-gray-600">
          ${maxPrice.toFixed(0)}
        </text>
        <text x="10" y={svgHeight - padding} textAnchor="start" className="text-xs fill-gray-600">
          ${minPrice.toFixed(0)}
        </text>
        
        {/* X-axis labels */}
        {points.length > 1 && (
          <>
            <text x={padding} y={svgHeight - 10} textAnchor="start" className="text-xs fill-gray-600">
              {data[0].name}
            </text>
            <text x={svgWidth - padding} y={svgHeight - 10} textAnchor="end" className="text-xs fill-gray-600">
              {data[data.length - 1].name}
            </text>
          </>
        )}
      </svg>
    </div>
  );
};