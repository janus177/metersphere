import { TableQueryParams } from '@/models/common';
import { WorkCardEnum } from '@/enums/workbenchEnum';

// 配置卡片列表
export interface WorkConfigItem {
  label: string;
  value: string;
  description?: string;
  img?: string;
  expand?: boolean;
}

export interface childrenWorkConfigItem extends WorkConfigItem {
  value: WorkCardEnum;
}

export interface WorkConfigCard extends WorkConfigItem {
  children: childrenWorkConfigItem[];
}

export interface SelectedCardItem {
  label: string;
  id: string; // 唯一id
  key: WorkCardEnum;
  fullScreen: boolean; // 是否全屏
  isDisabledHalfScreen: boolean; // 是否禁用半屏幕
  pos?: number; // 排序
  projectIds: string[];
  handleUsers: string[];
}

// 查询入参
export interface WorkHomePageDetail extends TableQueryParams {
  dayNumber: number | string;
  startTime: number | null;
  endTime: number | null;
  projectIds: string[];
  handleUsers?: string[];
  organizationId: string;
}

export interface WorkTestPlanDetail {
  dayNumber: number | string;
  startTime: number | null;
  endTime: number | null;
  projectId: string;
}
export interface WorkTestPlanRageDetail {
  unExecute: number;
  executed: number;
  passed: number;
  notPassed: number;
  finished: number;
  running: number;
  prepared: number;
  archived: number;
  errorCode: number;
}

export interface TimeFormParams {
  dayNumber: number | string;
  startTime: number;
  endTime: number;
}

export interface OverViewOfProject {
  caseCountMap: Record<string, number>; // 模块列表
  projectCountList: {
    id: string;
    name: string;
    count: number[];
  }[]; // 项目列表
  xaxis: string[]; // 横坐标
}

export interface ModuleCardItem {
  label: string;
  value: string | number;
  count?: number;
  icon?: string;
  color?: string;
  [key: string]: any;
}

export type StatusStatisticsMapType = Record<
  string,
  {
    name: string;
    count: number;
  }[]
>;

export interface PassRateDataType {
  statusStatisticsMap: StatusStatisticsMapType | null;
  statusPercentList:
    | {
        status: string; // 状态
        count: number;
        percentValue: string; // 百分比
      }[]
    | null;
  errorCode: number;
}

export interface ApiCoverageData {
  allApiCount: number; // 总的 API 数量
  unCoverWithApiDefinition: number; // 未覆盖 API 定义的数量
  coverWithApiDefinition: number; // 覆盖了 API 定义的数量
  apiCoverage: string; // API 覆盖率

  unCoverWithApiCase: number; // 未覆盖 API 测试用例的数量
  coverWithApiCase: number; // 覆盖了 API 测试用例的数量
  apiCaseCoverage: string; // API 测试用例覆盖率（

  unCoverWithApiScenario: number; // 未覆盖 API 场景的数量
  coverWithApiScenario: number; // 覆盖了 API 场景的数量
  scenarioCoverage: string; // API 场景覆盖率
}