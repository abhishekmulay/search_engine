<template>
  <div class="results">
    <div class="result-item" v-for="(item, index) in items">
      <h4>
        <a :href="item.url" target="_blank" :title="item.title">
          <span v-html="item.title_h"></span>
        </a>
        <el-tooltip :content="'Link depth: ' + item.depth" placement="right">
          <span class="badge">{{ item.depth }}</span>
        </el-tooltip>
        <span class="authors pill"> {{ item.author }} </span>
      </h4>

      <p class="result-url" v-html="item.url_h"></p>
      <p class="result-desc" v-if="item.exerpt">
        <span class="result-date" v-if="item.pretty_date">{{ item.pretty_date }} -</span>
        <span class="result-exerpt" v-html="item.exerpt"></span>
      </p>
    </div>
    <div class="no-result" v-if="items.length == 0">No more results can be found.</div>
  </div>
</template>

<script>
export default {
  props: ['items', 'query'],
}
</script>

<style lang="sass">
.results
  margin: 10px 0 20px;
.results-count
  margin: 0 0 20px 1px;
  font-size: 13px;
  color: #888;
.result-item
  margin-bottom: 25px;
  // margin-bottom: 15px;
  // padding: 0.5em 1em;
  // border: 1px solid #f0f0f0;
  // box-shadow: 0 2px 0 rgba(0,0,0,0.04);
  .badge
    background: #f3f3f3;
    border-radius: 50%;
    display: inline-block;
    color: #aaa;
    line-height: 1;
    width: 1em;
    text-align: center;
    padding: 4px;
    font-size: 13px;
    vertical-align: top;
  em
    font-weight: 600;
    font-style: normal;
  h4
    line-height: 1.3em;
    margin: 0;
    font-weight: 400;
    font-size: 17px;
  p
    line-height: 1.5em;
    margin: 8px 0;
    font-size: 12px;
  .result-url
    margin: 0;
    color: #006621;
    font-size: 14px;
  .result-url, h4 a
    display: inline-block;
    max-width: 100%;
    text-overflow: ellipsis;
    max-height: 1.3em;
    overflow: hidden;
    white-space: nowrap;
  h4 a
    max-width: 95%;
.result-date
  display: inline-block;
  color: #666;
  margin-right: 2px;
.authors
  font-size: 12px;
  position: relative;
  text-transform : capitalize;
  color: #777;
  top: -5px;
.pill
  border: 1px solid #E2E5EB;
  border-radius: 5px;
  background-color: #EDEFF3;
  color: #8697AF;
  font-size: 11px;
  padding: 0px 5px;
  white-space: nowrap;
  text-transform: capitalize;
  /* margin: 0 5px 5px 0; */
  display: inline-block;

</style>
