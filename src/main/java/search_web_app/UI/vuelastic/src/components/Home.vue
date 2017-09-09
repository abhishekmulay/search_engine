<template>
  <el-row>
    <el-col :span="18" :offset="6" :xs="{offset:0, span:24}">
      <div class="home container">
        <h1 class="text-center">Elastic4 Search</h1>
        <h4 class="text-center">The fantastic search engine.</h4>
        <search-form class="big-search" icon="search" />
      </div>
    </el-col>
  </el-row>
</template>

<script>

export default {
}
</script>

<style lang="scss">
  h1 {
    font-size : 50px;
  }
  h4 {
    font-size: 20px;
    font-weight: 300;
  }
  .text-center {
    width : 600px;
    margin: 10px;
    text-align : center;
  }
</style>
