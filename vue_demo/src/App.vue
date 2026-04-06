<template>
  <div id="app">
    <nav>
      <router-link to="/login"></router-link>
    </nav>
    <router-view />
    <!-- 全局可拖动主题切换按钮 -->
    <div
        class="global-theme-toggle"
        :style="{ left: pos.x + 'px', top: pos.y + 'px' }"
        @mousedown="onMouseDown"
        @touchstart.prevent="onTouchStart"
        @click.capture="onClickCapture"
    >
      <ThemeToggle />
    </div>
  </div>
</template>

<script>
import ThemeToggle from '@/components/ui/ThemeToggle.vue'

export default {
  components: { ThemeToggle },
  data() {
    return {
      pos: { x: 24, y: window.innerHeight - 80 },
      dragging: false,
      dragStarted: false,
      wasDragged: false,
      offset: { x: 0, y: 0 },
      startPos: { x: 0, y: 0 },
      DRAG_THRESHOLD: 5
    }
  },
  methods: {
    onMouseDown(e) {
      this.dragStarted = false
      this.startPos = { x: e.clientX, y: e.clientY }
      this.offset.x = e.clientX - this.pos.x
      this.offset.y = e.clientY - this.pos.y
      window.addEventListener('mousemove', this.onMouseMove)
      window.addEventListener('mouseup', this.onMouseUp)
    },
    onMouseMove(e) {
      const dx = e.clientX - this.startPos.x
      const dy = e.clientY - this.startPos.y
      if (!this.dragStarted && Math.sqrt(dx * dx + dy * dy) < this.DRAG_THRESHOLD) return
      this.dragStarted = true
      this.dragging = true
      this.wasDragged = true
      this.pos.x = Math.min(Math.max(0, e.clientX - this.offset.x), window.innerWidth - 100)
      this.pos.y = Math.min(Math.max(0, e.clientY - this.offset.y), window.innerHeight - 60)
    },
    onMouseUp() {
      this.dragging = false
      this.dragStarted = false
      window.removeEventListener('mousemove', this.onMouseMove)
      window.removeEventListener('mouseup', this.onMouseUp)
    },
    onTouchStart(e) {
      const touch = e.touches[0]
      this.dragStarted = false
      this.startPos = { x: touch.clientX, y: touch.clientY }
      this.offset.x = touch.clientX - this.pos.x
      this.offset.y = touch.clientY - this.pos.y
      window.addEventListener('touchmove', this.onTouchMove, { passive: false })
      window.addEventListener('touchend', this.onTouchEnd)
    },
    onTouchMove(e) {
      const touch = e.touches[0]
      const dx = touch.clientX - this.startPos.x
      const dy = touch.clientY - this.startPos.y
      if (!this.dragStarted && Math.sqrt(dx * dx + dy * dy) < this.DRAG_THRESHOLD) return
      e.preventDefault()
      this.dragStarted = true
      this.dragging = true
      this.wasDragged = true
      this.pos.x = Math.min(Math.max(0, touch.clientX - this.offset.x), window.innerWidth - 100)
      this.pos.y = Math.min(Math.max(0, touch.clientY - this.offset.y), window.innerHeight - 60)
    },
    onTouchEnd() {
      this.dragging = false
      this.dragStarted = false
      window.removeEventListener('touchmove', this.onTouchMove)
      window.removeEventListener('touchend', this.onTouchEnd)
    },
    onClickCapture(e) {
      if (this.wasDragged) {
        e.stopPropagation()
        this.wasDragged = false
      }
    }
  }
}
</script>

<style>
.global-theme-toggle {
  position: fixed;
  z-index: 9999;
  cursor: grab;
  user-select: none;
}

.global-theme-toggle:active {
  cursor: grabbing;
}
</style>