import { Injectable } from '@angular/core';
import Chart, { ChartType, ChartConfiguration } from 'chart.js/auto';

export interface ChartDataset {
  label: string;
  data: any[]; // Supports numbers or objects like {x, y, r} for Bubble/Scatter
  color?: string;
  fill?: boolean;
  borderDash?: number[];
  stack?: string; // For stacked bar charts
}

export interface DiagramConfig {
  canvasId: string;
  /**
   * Supported types: standard ChartType or custom aliases
   */
  type: ChartType | 'bar_vertical' | 'bar_horizontal' | 'semi_circle' | 'doughnut_ring' | 'pie_chart' | 'stacked_bar' | 'scatter_plot' | 'bubble_chart' | 'radar_chart' | 'polar_area';
  title: string;
  labels?: string[];
  datasets: ChartDataset[];
  unit?: string;
  xAxisLabel?: string;
  yAxisLabel?: string;
}

@Injectable({ providedIn: 'root' })
export class DiagramService {

  private defaultColors = [
    'rgba(54, 162, 235, 0.7)', 'rgba(75, 192, 192, 0.7)', 'rgba(255, 159, 164, 0.7)',
    'rgba(255, 99, 132, 0.7)', 'rgba(153, 102, 255, 0.7)', 'rgba(255, 205, 86, 0.7)',
    'rgba(71, 85, 105, 0.7)', 'rgba(20, 184, 166, 0.7)'
  ];

  /**
   * Build any diagram from a given configuration
   */
  createChart(config: DiagramConfig): Chart | null {
    const ctx = document.getElementById(config.canvasId) as HTMLCanvasElement;
    if (!ctx) {
      console.error(`Canvas with ID '${config.canvasId}' not found.`);
      return null;
    }

    const chartType = this.mapType(config.type);
    const options = this.getBaseOptions(config);

    // Specific logic for horizontal bars
    if (config.type === 'bar_horizontal') {
      (options as any).indexAxis = 'y';
    }

    // Specific logic for semi-circle pie/doughnut
    if (config.type === 'semi_circle') {
      (options as any).circumference = 180;
      (options as any).rotation = -90;
    }

    // Enable stacking for stacked_bar
    if (config.type === 'stacked_bar') {
      options.scales.x.stacked = true;
      options.scales.y.stacked = true;
    }

    const chartConfig: ChartConfiguration = {
      type: chartType,
      data: {
        labels: config.labels,
        datasets: config.datasets.map((ds, index) => ({
          label: ds.label,
          data: ds.data,
          backgroundColor: this.getBackground(config.type, ds.color, index),
          borderColor: ds.color || this.defaultColors[index % this.defaultColors.length].replace('0.7', '1'),
          borderWidth: chartType === 'radar' ? 3 : 2,
          fill: ds.fill ?? (chartType === 'radar' || chartType === 'scatter' ? false : true),
          borderDash: ds.borderDash || [],
          tension: 0.3,
          stack: ds.stack
        }))
      },
      options: options
    };

    try {
      const existingChart = Chart.getChart(config.canvasId);
      if (existingChart) existingChart.destroy();

      return new Chart(ctx, chartConfig);
    } catch (error) {
      console.error('Diagram_Forge_Error:', error);
      return null;
    }
  }

  private mapType(type: string): ChartType {
    switch (type) {
      case 'bar_vertical':
      case 'bar_horizontal':
      case 'stacked_bar': return 'bar';
      case 'semi_circle':
      case 'pie_chart': return 'pie';
      case 'doughnut_ring': return 'doughnut';
      case 'radar_chart': return 'radar';
      case 'polar_area': return 'polarArea';
      case 'scatter_plot': return 'scatter';
      case 'bubble_chart': return 'bubble';
      default: return type as ChartType;
    }
  }

  private getBackground(type: string, color: string | undefined, index: number) {
    const multiColorTypes = ['pie', 'doughnut', 'polarArea', 'pie_chart', 'doughnut_ring', 'polar_area', 'semi_circle'];
    if (multiColorTypes.includes(type)) {
      return this.defaultColors;
    }
    return color || this.defaultColors[index % this.defaultColors.length];
  }

  private getBaseOptions(config: DiagramConfig): any {
    const chartType = this.mapType(config.type);
    const isRadial = ['radar', 'polarArea'].includes(chartType);
    const isPieDoughnut = ['pie', 'doughnut'].includes(chartType);
    const unit = config.unit ? ` ${config.unit}` : '';

    const options: any = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: config.title,
          font: { size: 14, weight: '900' }
        },
        legend: {
          position: (isPieDoughnut || isRadial) ? 'right' : 'top',
          labels: { boxWidth: 10, font: { size: 10 } }
        },
        tooltip: {
          backgroundColor: '#1e293b',
          callbacks: {
            label: (context: any) => {
              const label = context.dataset.label || '';
              const val = context.parsed.y ?? context.parsed.r ?? context.parsed;
              return ` ${label}: ${val.toLocaleString()}${unit}`;
            }
          }
        }
      }
    };

    if (isRadial) {
      options.scales = {
        r: {
          angleLines: { display: true },
          suggestedMin: 0,
          ticks: { backdropColor: 'transparent', font: { size: 8 } }
        }
      };
    } else if (!isPieDoughnut) {
      options.scales = {
        y: {
          beginAtZero: true,
          title: { display: !!config.yAxisLabel, text: config.yAxisLabel },
          ticks: {
            font: { size: 9 },
            callback: (val: any) => val.toLocaleString() + unit
          }
        },
        x: {
          title: { display: !!config.xAxisLabel, text: config.xAxisLabel },
          ticks: { font: { size: 9 } }
        }
      };
    }

    return options;
  }
}
