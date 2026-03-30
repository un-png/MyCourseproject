<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>广告总访问量</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ visitCount }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>内容数量</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ articleCount }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>评论数量</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ commentCount }}</h2>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <span>用户数量</span>
            </div>
          </template>
          <div class="card-content">
            <h2>{{ userCount }}</h2>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>内容分布</span>
            </div>
          </template>
          <div class="chart-content" ref="pieChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

// 统计数据
const visitCount = ref(0)
const articleCount = ref(0)
const commentCount = ref(0)
const userCount = ref(0)
const pieChartRef = ref(null)
let pieChart = null
const pieChartData = ref([])

// 定时器ID
let timer = null

// 获取所有数据的方法
const fetchAllData = () => {
  getVisitCount()
  getArticleCount()
  getCommentCount()
  getUserCount()
  getContentDistribution()
}

// 获取访问量
const getVisitCount = async () => {
  try {
    const response = await axios.get('/dashboard/browseAccount')
    visitCount.value = response.data.data
  } catch (error) {
    console.error('获取访问量数据失败:', error);
    ElMessage.error('获取访问量数据失败')
  }
}

// 获取内容数量
const getArticleCount = async () => {
  try {
    const response = await axios.get('/dashboard/articleAccount')
    articleCount.value = response.data.data
  } catch (error) {
    console.error('获取内容数量失败:', error);
    ElMessage.error('获取内容数量失败')
  }
}

// 获取评论数量
const getCommentCount = async () => {
  try {
    const response = await axios.get('/dashboard/commentAccount')
    commentCount.value = response.data.data
  } catch (error) {
    console.error('获取评论数量失败:', error);
    ElMessage.error('获取评论数量失败')
  }
}

// 获取用户数量
const getUserCount = async () => {
  try {
    const response = await axios.get('/dashboard/userAccount')
    userCount.value = response.data.data
  } catch (error) {
    console.error('获取用户数量失败:', error);
    ElMessage.error('获取用户数量失败')
  }
}

// 获取内容分布数据并存储
const getContentDistribution = async () => {
  try {
    const response = await axios.get('/dashboard/contentDistribution')
    const data = response.data.data
    console.log("内容分布数据:", data);
    pieChartData.value = data
    // 获取到新数据后，如果图表实例存在，则更新图表
    if (pieChart) {
        updatePieChart(pieChartData.value)
    } else {
        // 如果图表实例还不存在，但在获取到数据时容器已准备好，尝试初始化并设置数据
         if (pieChartRef.value && pieChartRef.value.offsetWidth > 0 && pieChartRef.value.offsetHeight > 0) {
             initAndSetPieChart(pieChartData.value)
         }
    }
  } catch (error) {
    console.error('获取内容分布数据失败:', error);
    ElMessage.error('获取内容分布数据失败')
  }
}

// 初始化并设置饼图选项
const initAndSetPieChart = (data) => {
    const chartElement = pieChartRef.value;

    if (!chartElement) {
        console.warn("饼图容器DOM元素未找到，延迟初始化/更新");
        // 如果容器不存在，稍后再次尝试
        setTimeout(() => initAndSetPieChart(data), 100);
        return;
    }

    // 确保容器有尺寸后再初始化或更新
    if (chartElement.offsetWidth === 0 || chartElement.offsetHeight === 0) {
        console.warn("饼图容器DOM元素尺寸为零，延迟初始化/更新");
        // 使用requestAnimationFrame结合setTimeout延迟，更适应浏览器渲染周期
        requestAnimationFrame(() => {
             setTimeout(() => initAndSetPieChart(data), 50);
        });
        return;
    }

    // 如果图表实例不存在，则初始化
    if (!pieChart) {
        pieChart = echarts.init(chartElement);
        console.log("ECharts 饼图实例初始化成功");
        // 监听窗口大小变化，只有在初始化时添加一次监听器
        window.addEventListener('resize', handleResize);
    }

    // 设置图表选项
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: data.map(item => item.name || item.category)
        },
        series: [
            {
                name: '内容分布',
                type: 'pie',
                radius: ['50%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: '20',
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: data.map(item => ({
                    value: item.value,
                    name: item.name || item.category
                }))
            }
        ]
    };

    pieChart.setOption(option);
    console.log("ECharts 饼图选项设置成功");
};

// 更新饼图（供getContentDistribution在图表已存在时调用）
const updatePieChart = (data) => {
     if (pieChart) {
        // 仅更新数据和图例
        pieChart.setOption({
             legend: {
                 data: data.map(item => item.name || item.category)
             },
             series: [
                 {
                     data: data.map(item => ({
                         value: item.value,
                         name: item.name || item.category
                     }))
                 }
             ]
         });
         console.log("ECharts 饼图数据更新成功");
     } else {
         console.warn("饼图实例不存在，无法更新数据，尝试重新初始化并设置");
         // 如果因为某种原因实例丢失，尝试重新初始化并设置
         initAndSetPieChart(data);
     }
}

// 页面加载时获取所有数据并启动定时器
onMounted(() => {
  // 确保DOM完全渲染后再执行图表初始化和数据加载
  nextTick(() => {
     // 初始化图表容器 (可能没有数据，或者使用旧数据)
     // initAndSetPieChart([]) // 先尝试初始化空图或旧图，数据后续会更新

     // 直接获取所有数据，包括内容分布。
     // getContentDistribution 获取到数据后会调用 updatePieChart 或尝试 initAndSetPieChart
     fetchAllData();


     // 设置定时器，每分钟刷新一次
     timer = setInterval(fetchAllData, 60000); // 60000毫秒 = 1分钟
  });
});

// 处理窗口大小变化
const handleResize = () => {
  pieChart?.resize();
};


// 组件卸载时清除定时器和图表实例
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  if (pieChart) {
    pieChart.dispose(); // 销毁图表实例
    pieChart = null;
    console.log("ECharts 饼图实例已销毁");
  }
  // 移除窗口大小变化监听
  window.removeEventListener('resize', handleResize);
});

// 可以watch pieChartData的变化来触发图表更新 (备选方案，目前通过getContentDistribution控制更新)
// import { watch } from 'vue';
// watch(pieChartData, (newData) => {
//   if (pieChart) {
//     updatePieChart(newData);
//   } else {
//     initAndSetPieChart(newData); // 如果图表实例丢失，尝试重新初始化并设置新数据
//   }
// });

</script>

<style scoped>
.dashboard {
  height: 100%;
  padding: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  text-align: center;
}

.card-content h2 {
  font-size: 28px;
  margin: 10px 0;
  color: #303133;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  height: 400px;
}

.chart-content {
  height: 320px;
  width: 100%;
}
</style> 