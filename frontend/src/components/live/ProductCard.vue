<script setup>
defineProps({
  product: Object
})
</script>

<template>
  <div class="card">
    <div
      :style="{ background: `center / cover url(${product?.productThumbnailUrl}) no-repeat` }"
      class="thumbnail"
    ></div>
    <div class="right">
      <p class="title">{{ product?.title }}</p>
      <div>
        <span class="discountPrice">{{ product?.discountPrice.toLocaleString('ko-KR') }}원</span>
        <span class="price">{{ product?.price.toLocaleString('ko-KR') }}원</span>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.card {
  @include box(75%, 11rem, $light-gray, 1.25rem, 1rem 0, 1rem);
  @include flex-box(center, flex-start);
  .thumbnail {
    @include box(18rem, 9rem, null, 0.625rem, null, null);
  }
  .right {
    margin-left: 1rem;
    .title {
      @include font-factory($fs-4, bold);
      margin: 1rem 0;
    }
    .discountPrice {
      @include font-factory($fs-3, bold, $main);
      margin-right: 1rem;
    }
    .price {
      @include font-factory($fs-2, 500);
      text-decoration: line-through;
    }
  }
}
</style>
